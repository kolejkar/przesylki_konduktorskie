package karol.przesylki.konduktorskie_przesylki.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;


@Entity
@Data
public class Post_Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="client_id")
    private Integer id;

    private String name;

    private String surname;

    private String phone_nuber;

    private String adress;

    private String station;

    private Boolean collection_order;

}
