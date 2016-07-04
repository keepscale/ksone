package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/api")
public class BookingResource {

    private final Logger log = LoggerFactory.getLogger(BookingResource.class);

    @Inject
    private BookingRepository bookingRepository;

    /**
     * POST  /bookings -> Create a new booking.
     */
    @RequestMapping(value = "/bookings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> create(@Valid @RequestBody Booking booking) throws URISyntaxException {
        log.debug("REST request to save Booking : {}", booking);
        if (booking.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new booking cannot already have an ID").body(null);
        }
        Booking result = bookingRepository.save(booking);
        return ResponseEntity.created(new URI("/api/bookings/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("booking", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /bookings -> Updates an existing booking.
     */
    @RequestMapping(value = "/bookings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> update(@Valid @RequestBody Booking booking) throws URISyntaxException {
        log.debug("REST request to update Booking : {}", booking);
        if (booking.getId() == null) {
            return create(booking);
        }
        Booking result = bookingRepository.save(booking);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("booking", booking.getId().toString()))
                .body(result);
    }

    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/bookings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Booking>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Booking> page = bookingRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookings", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bookings/:id -> get the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> get(@PathVariable Long id) {
        log.debug("REST request to get Booking : {}", id);
        return Optional.ofNullable(bookingRepository.findOne(id))
            .map(booking -> new ResponseEntity<>(
                booking,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bookings/:id -> delete the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Booking : {}", id);
        bookingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("booking", id.toString())).build();
    }
}
