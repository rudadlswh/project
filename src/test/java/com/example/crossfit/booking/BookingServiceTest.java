package com.example.crossfit.booking;

import com.example.crossfit.common.Role;
import com.example.crossfit.member.Membership;
import com.example.crossfit.member.MembershipRepository;
import com.example.crossfit.member.MembershipType;
import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import com.example.crossfit.attendance.AttendanceService;
import com.example.crossfit.attendance.AttendanceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    void countMembershipDecrementsOnReserve() {
        User member = userRepository.save(new User("member1@test.local", "pw", Role.MEMBER, "회원1"));
        membershipRepository.save(new Membership(member, MembershipType.COUNT, LocalDate.now(), null, 1));
        Session session = createSession(LocalDate.now().plusDays(1), 5);

        Reservation reservation = bookingService.reserve(session.getId(), member.getId());

        assertEquals(ReservationStatus.RESERVED, reservation.getStatus());
        Membership updated = membershipRepository.findTopByUserIdOrderByIdDesc(member.getId()).orElseThrow();
        assertEquals(0, updated.getRemainingCount());
    }

    @Test
    void waitlistPromotionConsumesAndRefunds() {
        User member1 = userRepository.save(new User("member2@test.local", "pw", Role.MEMBER, "회원2"));
        User member2 = userRepository.save(new User("member3@test.local", "pw", Role.MEMBER, "회원3"));
        membershipRepository.save(new Membership(member1, MembershipType.COUNT, LocalDate.now(), null, 1));
        membershipRepository.save(new Membership(member2, MembershipType.COUNT, LocalDate.now(), null, 1));
        Session session = createSession(LocalDate.now().plusDays(1), 1);

        Reservation r1 = bookingService.reserve(session.getId(), member1.getId());
        Reservation r2 = bookingService.reserve(session.getId(), member2.getId());
        assertEquals(ReservationStatus.RESERVED, r1.getStatus());
        assertEquals(ReservationStatus.WAITLIST, r2.getStatus());

        bookingService.cancelReservation(r1.getId(), member1.getId());

        Reservation updatedR2 = reservationRepository.findBySessionIdAndUserId(session.getId(), member2.getId()).orElseThrow();
        assertEquals(ReservationStatus.RESERVED, updatedR2.getStatus());
        Membership m1 = membershipRepository.findTopByUserIdOrderByIdDesc(member1.getId()).orElseThrow();
        Membership m2 = membershipRepository.findTopByUserIdOrderByIdDesc(member2.getId()).orElseThrow();
        assertEquals(1, m1.getRemainingCount());
        assertEquals(0, m2.getRemainingCount());
    }

    @Test
    void expiredPeriodMembershipBlocksReserve() {
        User member = userRepository.save(new User("member4@test.local", "pw", Role.MEMBER, "회원4"));
        membershipRepository.save(new Membership(member, MembershipType.PERIOD, LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1), null));
        Session session = createSession(LocalDate.now().plusDays(1), 3);

        assertThrows(ResponseStatusException.class, () -> bookingService.reserve(session.getId(), member.getId()));
    }

    @Test
    void attendanceIsIdempotent() {
        User member = userRepository.save(new User("member5@test.local", "pw", Role.MEMBER, "회원5"));
        LocalDate today = LocalDate.now();

        attendanceService.markAttendance(member.getId(), today);
        attendanceService.markAttendance(member.getId(), today);

        assertEquals(1, attendanceRepository.findByUserAndAttendedDate(member, today).stream().count());
    }

    @Test
    void cancellationDeadlineIsEnforced() {
        User member = userRepository.save(new User("member6@test.local", "pw", Role.MEMBER, "회원6"));
        membershipRepository.save(new Membership(member, MembershipType.COUNT, LocalDate.now(), null, 1));
        TimeSlot slot = TimeSlot.SLOT_0900;
        LocalDate date = LocalDate.now();
        Session session = sessionRepository.save(new Session(
                date,
                slot,
                3,
                LocalDateTime.now().minusMinutes(10)
        ));

        Reservation reservation = bookingService.reserve(session.getId(), member.getId());

        assertThrows(ResponseStatusException.class,
                () -> bookingService.cancelReservation(reservation.getId(), member.getId()));
    }

    private Session createSession(LocalDate date, int capacity) {
        TimeSlot slot = TimeSlot.SLOT_0900;
        LocalDateTime cutoff = LocalDateTime.of(date, slot.getTime()).minusHours(1);
        return sessionRepository.save(new Session(date, slot, capacity, cutoff));
    }
}
