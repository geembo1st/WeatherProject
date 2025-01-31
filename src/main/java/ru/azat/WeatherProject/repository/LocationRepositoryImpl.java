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
        return entityManager.createQuery("SELECT l FROM Location l LEFT JOIN FETCH l.users", Location.class).getResultList();
    }

    @Override
    public void addLocation(Location location) {
        if (location.getId() == null) {
            entityManager.persist(location);
            System.out.println("Persisting new location: " + location);
        } else {
            entityManager.merge(location);
            System.out.println("Merging updated location: " + location);
        }
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

    @Override
    public List<Location> findByUserId(Long userId) {
        String query = "SELECT l FROM Location l JOIN l.users u WHERE u.id = :userId";
        return entityManager.createQuery(query, Location.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
