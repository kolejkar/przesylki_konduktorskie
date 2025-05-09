package karol.przesylki.konduktorskie_przesylki.tables;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import lombok.Data;
import lombok.Singular;

@Entity
@Data
public class Conductor {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "company_id")
	private Integer id;
	
	@NotEmpty
    @Email
	private String conductor_indeficator;
	
	@NotEmpty
	@Size(min = 8, max = 64, message = "Password must be 8-64 char long")
	private String password;
	
	@NotEmpty
	private String name;
	
	@Singular
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", 
    	joinColumns = @JoinColumn(name = "company_id"),
    	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> role = new HashSet();
}
