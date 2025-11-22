package karol.przesylki.konduktorskie_przesylki.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import karol.przesylki.konduktorskie_przesylki.repository.ConductorRepository;
import karol.przesylki.konduktorskie_przesylki.tables.Conductor;

public class ConductorDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ConductorRepository conductorRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        Conductor conductor = conductorRepo.findByConductorIndeficator(username).get();

        if (conductor == null)
        {
            throw new UsernameNotFoundException("Podany conductor nie istnieje skontaktuj się z administracją.");
        }
        return new ConductorDetails(conductor);
    }


}
