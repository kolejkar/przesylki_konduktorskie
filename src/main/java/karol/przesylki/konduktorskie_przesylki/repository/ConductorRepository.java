package karol.przesylki.konduktorskie_przesylki.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import karol.przesylki.konduktorskie_przesylki.tables.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

    @Cacheable
	Optional<Conductor> findByConductorIndeficator(String conductor_indeficator);
}
