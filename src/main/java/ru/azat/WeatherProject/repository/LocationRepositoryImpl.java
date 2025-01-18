package ru.azat.WeatherProject.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.azat.WeatherProject.model.Location;

import java.util.List;

@Repository
public class LocationRepositoryImpl implements LocationRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Location> getAllLocations() {
        return entityManager.createQuery("From Location").getResultList();
    }

    @Override
    public void addLocation(Location location) {
        entityManager.persist(location);
    }

    @Override
    public void deleteLocation(Long id) {
        Location location = showLocationById(id);
        if(location != null) {
            entityManager.remove(location);
        } else {
            throw new EntityNotFoundException("Location not found ");
        }
    }

    @Override
    public Location updateLocation(Long id, Location location) {
        return entityManager.merge(location);
    }

    @Override
    public Location showLocationById(Long id) {
        return entityManager.find(Location.class, id);
    }
}
