package theater.services;

import theater.helper.SectorInfo;
import theater.persist.dtos.*;
import theater.persist.model.EventRealizationEntity;
import theater.persist.model.PlaceEntity;
import theater.persist.model.PriceListEntity;

import java.util.Date;
import java.util.List;

public interface IEventService {


    List<PlaceDTO> getAllPlaces();

    List<EventRealizationDTO> getAllEventRealization();

    List<ReservationDTO> getAllEventReservation();

    List<SectorDTO> getAllSectorsByRealizationId(int id);

    List<ReservationDTO> getAllEventReservationByRealization(int id);

    List<ReservationDTO> getAllEventReservation(Integer id);

    List<PriceListDTO> getAllPriceList();

    ReservationDTO getReservationById(Integer id);

    EventRealizationDTO getEventRealizationById(Integer id);

    List<SectorInfo> getRoomInfo(Integer realizationId);

    List<PlaceDTO> convertSelectedSeatsStringArrayToPlaceList(String[] seats);
    List<PlaceEntity> convertSelectedSeatsStringArrayToPlaceEntityList(String[] seats);

    List<PlaceDTO> getPlaceListFromIds(String[] placeIds);

    void updateReservation(Integer reservationId, ReservationDTO reservation);


    void deleteReservation(Integer id);


    List<PlaceEntity> convertToEntity(List<PlaceDTO> placeDTOList);

    EventRealizationEntity convertToEntity(EventRealizationDTO eventRealizationDTO);

    void addReservation(ReservationDTO reservation, EventRealizationDTO eventRealization, List<PlaceDTO> selectedPlaces);

    void addTicket(EventRealizationDTO eventRealization, List<PlaceDTO> selectedPlaces);

    PriceListEntity getPriceListForEvent(Integer eventId, Date date);
}

