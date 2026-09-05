package com.example.lockingdemo.controller;

import com.example.lockingdemo.service.OptimisticSeatBookingTestService;
import com.example.lockingdemo.service.PessimisticSeatBookingTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingTestController {

    private final OptimisticSeatBookingTestService optimisticSeatBookingTestService;
    private final PessimisticSeatBookingTestService pessimisticSeatBookingTestService;

    @GetMapping("/optimistic/{seatId}")
    public String testOptimistic(@PathVariable Long seatId) throws InterruptedException {
        optimisticSeatBookingTestService.testOptimisticLocking(seatId);
        return "Optimistic locking test started! Check logs for results.";
    }

    @GetMapping("/pessimistic/{seatId}")
    public String testPessimistic(@PathVariable Long seatId) throws InterruptedException {
        pessimisticSeatBookingTestService.testPessimisticLocking(seatId);
        return "Pessimistic locking test started! Check logs for results.";
    }
}
