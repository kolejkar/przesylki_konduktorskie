package karol.przesylki.konduktorskie_przesylki.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import karol.przesylki.konduktorskie_przesylki.tables.Postbox;

public interface PostBoxRepository extends JpaRepository<Postbox, Integer> {

}
