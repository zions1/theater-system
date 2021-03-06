package theater.controller;

import com.google.gson.Gson;
import com.sun.javafx.collections.ObservableListWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import theater.helper.DateConverter;
import theater.helper.SectorInfo;
import theater.persist.daos.EventRealizationDAO;
import theater.persist.daos.PlaceDAO;
import theater.persist.dtos.*;
import theater.persist.model.PlaceEntity;
import theater.persist.model.PriceListEntity;
import theater.services.IEventService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Controller for handling all comunication with user via web server.
 */
@Controller
public class EventController {

    @Autowired
    private IEventService eventService;

    /**
     * Handling price list page.
     *
     * @param model
     * @return priceList
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/priceList"}, method = RequestMethod.GET)
    public String priceList(Model model) {
        model.addAttribute("priceList", eventService.getAllPriceList());
        return "priceList";
    }

    /**
     * Handling access denied page.
     *
     * @param model
     * @return accesDenied
     */
    @RequestMapping(value = {"/accessDenied"}, method = RequestMethod.GET)
    public String accessDenied(Model model) {
        return "accessDenied";
    }

    /**
     * Handling price list edit page.
     *
     * @param model
     * @param priceListID
     * @return editPriceList
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/editPriceList"}, method = RequestMethod.GET)
    public String editPriceList(Model model, @RequestParam(value = "priceListId", required = false) Integer priceListID) {
        PriceListDTO priceList = eventService.getPriceListById(priceListID);
        if (priceList != null) {
            EventDTO event = eventService.getEventById(priceList.getEventId());
            if (event != null) {
                model.addAttribute("priceList", priceList);
                model.addAttribute("events", eventService.getAllEvents());
            }
        }
        return  "editPriceList";
    }

    /**
     * Handling request from user to edit price list page.
     *
     * @param priceListId
     * @param priceListFrom
     * @param priceListTo
     * @param priceListName
     * @param event
     * @return redirect to priceList
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/editPriceList"}, method = RequestMethod.POST)
    public String editPriceList(@RequestParam("priceListId") String priceListId, @RequestParam("priceListFrom") String priceListFrom, @RequestParam("priceListTo") String priceListTo, @RequestParam("priceListName") String priceListName, @RequestParam("event") String event) {
        Integer eventId = Integer.parseInt(event);
        eventService.updatePriceList(Integer.parseInt(priceListId), priceListFrom, priceListTo, priceListName, eventId);
        return "redirect:/priceList";
    }


    /**
     * Handling adding new price list.
     *
     * @param model
     * @param priceListId
     * @return addPriceList
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/addPriceList"}, method = RequestMethod.GET)
    public String addPriceList(Model model, @RequestParam(value = "priceListId", required = false) String priceListId) {
        model.addAttribute("events", eventService.getAllEvents());
        PriceListDTO priceList = new PriceListDTO();
        model.addAttribute("priceList", priceList);
        return "addPriceList";
    }

    /**
     * Handling request from user to add price list page.
     *
     * @param priceListFrom
     * @param priceListTo
     * @param priceListName
     * @param event
     * @return redirect to priceList
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/addPriceList"}, method = RequestMethod.POST)
    public String addPriceList(@RequestParam("priceListFrom") String priceListFrom, @RequestParam("priceListTo") String priceListTo, @RequestParam("priceListName") String priceListName, @RequestParam("event") String event) {
        Integer eventId = Integer.parseInt(event);
        eventService.addPriceList(priceListFrom, priceListTo,priceListName, eventId);
        return "redirect:/priceList";
    }

    /**
     * Handling deleting price lists.
     *
     * @param priceListID
     * @return redirect to priceList
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/deletePriceList"}, method = RequestMethod.GET)
    public String deletePriceList(@RequestParam(value = "priceListId", required = true) Integer priceListID) {
        eventService.deletePriceList(priceListID);
        return "redirect:/priceList";
    }

    /**
     * Prepares event realizations list.
     *
     * @param model model to return model to return
     * @return eventRealizations view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER') or hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/eventRealizations"}, method = RequestMethod.GET)
    public String eventRealizations(Model model) {
        model.addAttribute("eventRealizationList", eventService.getAllEventRealization());
        return "eventRealizations";
    }

    /**
     * Handling events list.
     *
     * @param model
     * @return events
     */
    @PreAuthorize("hasRole('ROLE_CASHIER') or hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/events"}, method = RequestMethod.GET)
    public String events(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "events";
    }

    /**
     * Handling add realization page.
     *
     * @param model
     * @return addRealization
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/addRealization"}, method = RequestMethod.GET)
    public String addRealization(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("rooms", eventService.getAllRooms());
        return "addRealization";
    }

    /**
     * Handling request from user to add realization page.
     *
     * @param event
     * @param realizationDate
     * @param realizationTime
     * @param room
     * @return
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/addRealization"}, method = RequestMethod.POST)
    public String addRealization(@RequestParam("event") String event, @RequestParam("realizationDate") String realizationDate,
                                 @RequestParam("realizationTime") String realizationTime, @RequestParam("room") String room) {
        Integer eventId = Integer.parseInt(event);
        Integer roomId = Integer.parseInt(room);
        java.sql.Date eventRealizationDate = DateConverter.getInstance().stringToDate(realizationDate);
        Integer realizationHour = Integer.parseInt(realizationTime);
        eventService.addRealization(eventId, roomId, eventRealizationDate, realizationHour);
        return "redirect:/eventRealizations";
    }

    /**
     * Handling edit realization page.
     *
     * @param model
     * @param eventRealizationId
     * @return editRealization
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/editRealization"}, method = RequestMethod.GET)
    public String editRealization(Model model, @RequestParam(value = "realizationId", required = false) String eventRealizationId) {
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(Integer.parseInt(eventRealizationId));
        model.addAttribute("eventRealization", eventRealization);
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("rooms", eventService.getAllRooms());
        return "editRealization";
    }

    /**
     * Handling request from user to edit realization page.
     * @param event
     * @param realizationDate
     * @param realizationTime
     * @param room
     * @param realization
     * @return redirect to eventRealizations
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/editRealization"}, method = RequestMethod.POST)
    public String editRealization(@RequestParam("event") String event, @RequestParam("realizationDate") String realizationDate,
                                  @RequestParam("realizationTime") String realizationTime, @RequestParam("room") String room,
                                  @RequestParam("eventRealizationId") String realization) {
        Integer eventId = Integer.parseInt(event);
        Integer roomId = Integer.parseInt(room);
        Integer realizationId = Integer.parseInt(realization);
        java.sql.Date eventRealizationDate = DateConverter.getInstance().stringToDate(realizationDate);
        Integer realizationHour = Integer.parseInt(realizationTime);
        eventService.updateRealization(realizationId, eventId, roomId, eventRealizationDate, realizationHour);
        return "redirect:/eventRealizations";
    }

    /**
     * Handling deleting realizations.
     *
     * @param eventRealizationId
     * @return redirect to ecentRealizations
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/deleteRealization"}, method = RequestMethod.GET)
    public String deleteRealization(@RequestParam(value = "realizationId", required = true) Integer eventRealizationId) {
        eventService.deleteEventRealization(eventRealizationId);
        return "redirect:/eventRealizations";
    }

    /**
     * Handling adding events page.
     *
     * @param model
     * @param eventId
     * @return addEvent
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/addEvent"}, method = RequestMethod.GET)
    public String addEvent(Model model, @RequestParam(value = "eventId", required = false) String eventId) {
        model.addAttribute("eventTypes", eventService.getAllEventTypes());
        EventDTO event = new EventDTO();
        model.addAttribute("event", event);
        return "addEvent";
    }

    /**
     * Handling request from user to add event page.
     *
     * @param eventName
     * @param eventType
     * @param eventDescription
     * @return redirect to events
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/addEvent"}, method = RequestMethod.POST)
    public String addEvent(@RequestParam("eventName") String eventName, @RequestParam("eventType") String eventType,
                                 @RequestParam("eventDescription") String eventDescription) {
        Integer eventTypeId = Integer.parseInt(eventType);
        eventService.addEvent(eventName, eventTypeId, eventDescription);
        return "redirect:/events";
    }

    /**
     * Handling editing events
     *
     * @param model
     * @param eventId
     * @return editEvent
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/editEvent"}, method = RequestMethod.GET)
    public String editEvent(Model model, @RequestParam(value = "eventId", required = false) String eventId) {
        model.addAttribute("eventTypes", eventService.getAllEventTypes());
        EventDTO event = eventService.getEventById(Integer.parseInt(eventId));
        model.addAttribute("event", event);
        return "editEvent";
    }

    /**
     * Handling request from user to edit event page.
     *
     * @param eventId
     * @param eventName
     * @param eventType
     * @param eventDescription
     * @return redirect to events
     */
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/editEvent"}, method = RequestMethod.POST)
    public String editEvent(@RequestParam("eventId") String eventId, @RequestParam("eventName") String eventName, @RequestParam("eventType") String eventType,
                           @RequestParam("eventDescription") String eventDescription) {
        Integer eventTypeId = Integer.parseInt(eventType);
        eventService.updateEvent(Integer.parseInt(eventId), eventName, eventTypeId, eventDescription);
        return "redirect:/events";
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping(value = {"/deleteEvent"}, method = RequestMethod.GET)
    public String deleteEvent(@RequestParam(value = "eventId", required = true) Integer eventId) {
        eventService.deleteEvent(eventId);
        return "redirect:/events";
    }

    /**
     * Prepares event reservations data 
     * @param model model to return
     * @param realizationID id of event realization
     * @return eventReservations view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/eventReservations"}, method = RequestMethod.GET)
    public String eventReservations(Model model, @RequestParam(value = "realizationId", required = true) Integer realizationID) {

        model.addAttribute("eventReservationList", eventService.getAllEventReservationByRealization(realizationID));
        model.addAttribute("eventDescription", eventService.getEventRealizationById(realizationID));
        return "eventReservations";
    }

    /**
     * Deletes an event reservation
     * @param realizationID id of event realization
     * @param reservationId id of event reservation
     * @return redirect
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/deleteEventReservation"}, method = RequestMethod.GET)
    public String deleteEventReservation(@RequestParam(value = "realizationId", required = true) Integer realizationID,
                                         @RequestParam(value = "reservationId", required = true) Integer reservationId) {
        eventService.deleteReservation(reservationId);
        return "redirect:/eventReservations?realizationId=" + realizationID;
    }

    /**
     * Prepares data for selecting seats
     * @param model model to return
     * @param realizationId id of event realization
     * @return selectSeats view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/selectSeats"}, method = RequestMethod.GET)
    public String selectSeats(Model model, @RequestParam(value = "realizationId", required = false) Integer realizationId) {
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(realizationId);
        if (eventRealization != null) {
            model.addAttribute("event", eventRealization);
            return "selectSeats";
        }
        return "eventReservations";
    }


    /**
     * Prepares data to create reservation
     * @param model model to return
     * @param seats an array of pairs in string (coma separated): sectorNumber, placeNumber
     * @param realizationId id of event realization
     * @return createReservation view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/createReservation"}, method = RequestMethod.POST)
    public String createReservation(Model model, @RequestParam("seats") String[] seats, @RequestParam("realizationId") String realizationId) {
        Integer realizationIdInt = Integer.parseInt(realizationId);
        List<PlaceEntity> selectedPlaces = eventService.convertSelectedSeatsStringArrayToPlaceEntityList(seats);
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(realizationIdInt);
        if (eventRealization != null) {
            ReservationDTO reservation = new ReservationDTO();
            model.addAttribute("event", eventRealization);
            model.addAttribute("placesList", selectedPlaces);
            model.addAttribute("reservation", reservation);

            model.addAttribute("seats", seats);
            model.addAttribute("realizationId", realizationId);
            return "createReservation";
        }
        return "redirect:/eventRealizations";
    }

    /**
     * Creates new reservation
     * @param seats an array of pairs in string (coma separated): sectorNumber, placeNumber
     * @param realizationId id of event realization
     * @param reservation new reservation object
     * @param result binding result
     * @return
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/addReservation"}, method = RequestMethod.POST)
    public String addReservation(@RequestParam("seats") String[] seats, @RequestParam("realizationId") String realizationId,
                                 @ModelAttribute(value = "reservation") ReservationDTO reservation, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/editReservation?reservationId=" + reservation.getReservationId();
        }
        Integer realizationIdInt = Integer.parseInt(realizationId);
        List<PlaceDTO> selectedPlaces = eventService.convertSelectedSeatsStringArrayToPlaceList(seats);
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(realizationIdInt);
        if (eventRealization != null) {
            eventService.addReservation(reservation, eventRealization, selectedPlaces);//wysyłam miejsca
        }
        return "redirect:/eventReservations?realizationId=" + reservation.getEventRealizationId();
    }

    /**
     * Prepares reservation to edit
     * @param model model to return
     * @param reservationID id of event reservation
     * @return editReservation view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/editReservation"}, method = RequestMethod.GET)
    public String editReservation(Model model, @RequestParam(value = "reservationId", required = false) Integer reservationID) {
        ReservationDTO reservation = eventService.getReservationById(reservationID);
        if (reservation != null) {
            EventRealizationDTO event = eventService.getEventRealizationById(reservation.getEventRealizationId());
            List<PlaceEntity> places = (List<PlaceEntity>) reservation.getPlaces();
            if (event != null) {
                model.addAttribute("reservation", reservation);
                model.addAttribute("places", places);
                model.addAttribute("event", event);
            }
            return "editReservation";
        }
        return "eventRealizations";
    }

    /**
     * Updates reservation
     * @param reservationID id of event reservation
     * @param newReservation edited reservation object
     * @param result binding result
     * @return redirect
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/editReservation"}, method = RequestMethod.POST)
    public String editReservation(@RequestParam(value = "reservationId", required = false) Integer reservationID,
                                  @ModelAttribute(value = "reservation") ReservationDTO newReservation, BindingResult result) {
        ReservationDTO reservation = eventService.getReservationById(reservationID);
        if (result.hasErrors()) {
            return "redirect:/editReservation?reservationId=" + reservation.getReservationId();
        }
        eventService.updateReservation(reservationID, newReservation);
        return "redirect:/eventReservations?realizationId=" + reservation.getEventRealizationId();
    }

    /**
     * Prepares and returns json with room information
     * @param model model to return
     * @param realizationId id of event realization
     * @return roomInfo Json eg:
     * [
     *   {
     *       "name": 11,
     *       "rowsNumber": 2,
     *       "columnsNumber": 3,
     *       "occupiedSeats": [3,4]
     *   }
     * ]
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/roomInfo"}, method = RequestMethod.GET)
    public
    @ResponseBody
    List<SectorInfo> roomInfo(Model model, @RequestParam(value = "realizationId", required = false) Integer realizationId) {
        List<SectorInfo> roomInfo = eventService.getRoomInfo(realizationId);
        return roomInfo;
    }

    /**
     * Prepares ticket data from a reservation
     * @param model model to return
     * @param reservationId id of event reservation
     * @return tickets view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/tickets"}, method = RequestMethod.GET)
    public String tickets(Model model, @RequestParam(value = "reservationId", required = false) Integer reservationId) {
        ReservationDTO reservation = eventService.getReservationById(reservationId);
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(reservation.getEventRealizationId());
        PriceListEntity priceList = eventService.getPriceListForEvent(eventRealization.getEvent().getEventId(), eventRealization.getDate());
        model.addAttribute("priceList", priceList);
        model.addAttribute("numberOfTickets", reservation.getPlaces().size());
        model.addAttribute("places", reservation.getPlaces());
        model.addAttribute("reservationId", reservation.getReservationId());
        model.addAttribute("eventDescription", eventRealization);
        return "tickets";
    }

    /**
     * Prepares ticket data from selected places
     * @param model model to return
     * @param seats an array of pairs in string (coma separated): sectorNumber, placeNumber 
     * @param realizationId id of event realisation
     * @return tickets view
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/tickets"}, method = RequestMethod.POST)
    public String tickets(Model model, @RequestParam("seats") String[] seats, @RequestParam("realizationId") String realizationId) {
        Integer realizationIdInt = Integer.parseInt(realizationId);
        List<PlaceEntity> selectedPlaces = eventService.convertSelectedSeatsStringArrayToPlaceEntityList(seats);
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(realizationIdInt);
        PriceListEntity priceList = eventService.getPriceListForEvent(eventRealization.getEvent().getEventId(), eventRealization.getDate());
        Integer reservationId = -1;
        if (selectedPlaces.size() != 0) {
            model.addAttribute("priceList", priceList);
            model.addAttribute("numberOfTickets", selectedPlaces.size());
            model.addAttribute("places", selectedPlaces);
            model.addAttribute("reservationId", reservationId); // -1 means there's no reservation
            model.addAttribute("eventDescription", eventRealization);
            return "tickets";
        }
        return "redirect:/eventRealizations";
    }

    /**
     * Creates new ticket and removes reservation if exists
     * @param model model to return
     * @param placeIds an array of selected places ids
     * @param realizationId id of event realisation
     * @param reservationId id of event reservation
     * @return redirect
     */
    @PreAuthorize("hasRole('ROLE_CASHIER')")
    @RequestMapping(value = {"/addTickets"}, method = RequestMethod.POST)
    public String addTickets(Model model, @RequestParam("placeIds") String[] placeIds, @RequestParam("realizationId") String realizationId,
                             @RequestParam("reservationId") String reservationId) {
        if(Integer.parseInt(reservationId) != -1) {
            eventService.deleteReservation(Integer.parseInt(reservationId));
        }
        List<PlaceDTO> placeList = eventService.getPlaceListFromIds(placeIds);
        EventRealizationDTO eventRealization = eventService.getEventRealizationById(Integer.parseInt(realizationId));
        eventService.addTicket(eventRealization, placeList);
        return "redirect:/eventRealizations";
    }


    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setAutoGrowCollectionLimit(500);
    }
}
