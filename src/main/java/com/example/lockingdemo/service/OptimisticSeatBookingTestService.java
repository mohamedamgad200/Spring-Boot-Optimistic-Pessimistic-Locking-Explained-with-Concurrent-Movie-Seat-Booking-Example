package com.example.lockingdemo.service;

import com.example.lockingdemo.entity.Seat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimisticSeatBookingTestService {
    private final MovieTicketBookingService movieTicketBookingService;

    public void testOptimisticLocking(Long seatId) throws InterruptedException {
        Thread th1 = new Thread(() -> {
            try {
                log.info("Thread {} is attempting to book the seat ", Thread.currentThread().getName());
                Seat seat = movieTicketBookingService.bookSeat(seatId);
                log.info("Thread {} successfully booked the seat with version {} ", Thread.currentThread().getName(),seat.getVersion());

            } catch (Exception ex) {
                log.error("Thread {} Failed with error {}", Thread.currentThread().getName(), ex.getMessage());
            }
        });

        Thread th2 = new Thread(() -> {
            try {
                log.info("Thread {} is attempting to book the seat ", Thread.currentThread().getName());
                Seat seat = movieTicketBookingService.bookSeat(seatId);
                log.info("Thread {} successfully booked the seat with version {} ", Thread.currentThread().getName(),seat.getVersion());

            } catch (Exception ex) {
                log.error("Thread {} Failed with error {}", Thread.currentThread().getName(), ex.getMessage());
            }
        });

        th1.start();
        th2.start();
        th1.join();
        th2.join();
    }
}
