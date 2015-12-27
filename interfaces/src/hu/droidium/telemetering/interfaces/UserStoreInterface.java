package hu.droidium.telemetering.interfaces;

import java.util.List;

public interface UserStoreInterface {

	public boolean addUser(String userName, boolean superUser);
	public List<String> getUsers();
	public boolean hasUser(String user);
	public boolean isSuperUser(String userName);
	public String removeUser(String string);
}