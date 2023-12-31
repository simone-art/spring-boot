package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotRequestDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.AppProperties;
import com.api.parkingcontrol.services.MyBean;
import com.api.parkingcontrol.services.ParkingSpotService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
@PropertySource("custom.properties")
public class ParkingSpotController {

    @Autowired
    @Qualifier("parkingSpotServiceImpl")
    ParkingSpotService parkingSpotService;

    @Autowired
    private MyBean myBean;

    @Autowired
    private AppProperties appProperties;

    @Value("${app.name}")
    private String appName;

    @Value("${app.port}")
    private String appPort;

    @Value("${app.host}")
    private String appHost;

    @Value("${message}")
    private String message;

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotRequestDto parkingSpotRequestDto){
        //Validações de regras de negócio
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotRequestDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotRequestDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotRequestDto.getApartment(), parkingSpotRequestDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotRequestDto, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

//    @GetMapping
//    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
//        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
//    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
//        System.out.println("App name: " + appName);
//        System.out.println("App port: " + appPort);
//        System.out.println("App host: " + appHost);
//        myBean.method();
        System.out.println("App Name: " + appProperties.getName());
        System.out.println("App Port: " + appProperties.getPort());
        System.out.println("App Host: " + appProperties.getHost());
        System.out.println(message);
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid ParkingSpotRequestDto parkingSpotRequestDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
//        var parkingSpotModel = parkingSpotModelOptional.get();
//        parkingSpotModel.setParkingSpotNumber(parkingSpotRequestDto.getParkingSpotNumber());
//        parkingSpotModel.setLicensePlateCar(parkingSpotRequestDto.getLicensePlateCar());
//        parkingSpotModel.setModelCar(parkingSpotRequestDto.getModelCar());
//        parkingSpotModel.setBrandCar(parkingSpotRequestDto.getBrandCar());
//        parkingSpotModel.setColorCar(parkingSpotRequestDto.getColorCar());
//        parkingSpotModel.setApartment(parkingSpotRequestDto.getApartment());
//        parkingSpotModel.setBlock(parkingSpotRequestDto.getBlock());
        // Segunda forma de atualizar usando o copyProperties que
        // transforma o DTO para Model
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotRequestDto, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }


}
