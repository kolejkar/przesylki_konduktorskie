package karol.przesylki.konduktorskie_przesylki.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import karol.przesylki.konduktorskie_przesylki.tables.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

}
