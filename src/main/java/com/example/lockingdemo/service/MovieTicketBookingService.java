package com.example.lockingdemo.service;

import com.example.lockingdemo.entity.Seat;
import com.example.lockingdemo.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieTicketBookingService {
    private final SeatRepository seatRepository;

    @Transactional
    public Seat bookSeat(Long seatId) {
        //fetch the existing seat by id
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found with id " + seatId));
        log.info("Thread {} fetched seat with version {}", Thread.currentThread().getName(), seat.getVersion());
        if (seat.isBooked()) {
            throw new RuntimeException("Seat already booked !");
        }
        //booking seat
        seat.setBooked(true);
        //version check will occur here
        return seatRepository.save(seat);
    }

    @Transactional
    public void bookSeatWithPessimistic(Long seatId) {
        //fetch the existing seat with Pessimistic lock by id
        log.info("Thread {} is attempting to fetch the seat", Thread.currentThread().getName());
        Seat seat = seatRepository.findByIdAndLock(seatId);
        log.info("Thread {} acquired the lock for seat id", Thread.currentThread().getName());
        if (seat.isBooked()) {
            log.error("Thread {} failed Seat Id {} is already booked", Thread.currentThread().getName(), seatId);
            throw new RuntimeException("Seat already booked !");
        }
        log.info("Thread {}  booking the seat {}", Thread.currentThread().getName(), seatId);
        //booking seat
        seat.setBooked(true);
        seatRepository.save(seat);
        log.info("Thread {}  successfully book the seat with ID {}", Thread.currentThread().getName(), seatId);
    }
}
