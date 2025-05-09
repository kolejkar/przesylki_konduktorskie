package karol.przesylki.konduktorskie_przesylki.tables;

public enum ERole {

	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_CONDUKTOR("ROLE_CONDUKTOR");
	
	private final String role;

	ERole(String role) {
		this.role = role;
	}
	
	@Override
	public String toString()
	{
		return role;
	}
}
