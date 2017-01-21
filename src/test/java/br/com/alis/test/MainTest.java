package br.com.alis.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import br.com.alis.model.Permission;
import br.com.alis.model.Profile;
import br.com.alis.model.ProfilePermission;
import br.com.alis.model.User;
import br.com.alis.model.UserProfilePermission;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainTest {

	public static final String SERVER_URL = "http://localhost:8080/AuthorizationServer/";

	public static void main(String[] args) throws Exception {
		createAdmin();
		System.out.println("Logando como admin.");
		User user = login("admin", "admin");
		createUsers();
		getAllUsers(user);
		updateUser();
		getAllUsers(user);
		deleteUser();
		getAllUsers(user);
		createPermission();
		getAllPermissions();
		updatePermission();
		getAllPermissions();
		deletePermission();
		getAllPermissions();
		associatePermissions();
		getAllUserPermissions(user);
		desassociatePermissions();
		getAllUserPermissions(user);
		System.out.println("Logando com teste1 não admin.");
		user = login("teste1", "teste");
		createUsers();
		updateUser();
		deleteUser();
		getAllUsers(user);
		getAllPermissions();
		associatePermissions();
		desassociatePermissions();

		requestAssociation();
		requestDesassociation();
		getAllRequestedAssociations();
		getAllRequestedDesassociations();

		System.out.println("Logando com teste2 admin.");
		user = login("teste2", "teste");
		getAllRequestedAssociations();
		getAllRequestedDesassociations();
		associateSpecificPermission("teste1");
		getAllUserPermissions(user);
		desassociateSpecificPermission("teste1");
		getAllUserPermissions(user);
		getAllRequestedAssociations();
		getAllRequestedDesassociations();
		requestAssociation();
		getAllRequestedAssociations();
		requestDesassociation();
		getAllRequestedDesassociations();
	}

	private static void createAdmin() throws Exception {
		URL url = new URL(SERVER_URL + "rest/user/createadmin");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		User answer = new User();
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output, new TypeToken<User>() {
			}.getType());
		}

		conn.disconnect();
		System.out.println("Admin: ");

		System.out.println("	" + answer.getLogin());

	}

	private static User login(String login, String password) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User(login, password);

		User response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/login", user, User.class);

		if (response == null)
			throw new RuntimeException("Login e/ou senha inválidos.");
		
		return response;
	}

	private static void createPermission() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		Profile profile = new Profile();
		profile.setId(1L);
		profile.setName("Perfil somente para leitores");

		Permission permission = new Permission();
		permission.setId(1L);
		permission.setAccess(Permission.READ_ONLY);

		ProfilePermission pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		ProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/createpermission", pp,
				ProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Permissão não inserida.");

		profile = new Profile();
		profile.setName("Perfil somente para escritores");

		permission = new Permission();
		permission.setId(2L);
		permission.setAccess(Permission.WRITE);

		pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/createpermission", pp,
				ProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Permissão não inserida.");

		profile = new Profile();
		profile.setId(3L);
		profile.setName("Perfil para ser apagado");

		permission = new Permission();
		permission.setId(1L);
		permission.setAccess(Permission.READ_ONLY);

		pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/createpermission", pp,
				ProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Permissão não inserida.");

		profile = new Profile();
		profile.setId(4L);
		profile.setName("Perfil para arquivos abertos ao público");

		permission = new Permission();
		permission.setId(3L);
		permission.setAccess(Permission.PUBLIC);

		pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/createpermission", pp,
				ProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Permissão não inserida.");

	}

	private static List<ProfilePermission> getAllPermissions() throws Exception {

		URL url = new URL(SERVER_URL + "rest/permission/allpermissions");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output,
					new TypeToken<List<ProfilePermission>>() {
					}.getType());
		}

		conn.disconnect();
		System.out.println("Perfils: ");
		for (ProfilePermission pp : answer) {

			System.out.println("	Nome: " + pp.getProfile().getName()
					+ " Permissão: " + pp.getPermission().getAccess());
		}

		return answer;
	}

	private static void updatePermission() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		Profile profile = new Profile();
		profile.setId(3L);
		profile.setName("Perfil a ser apagado");

		Permission permission = new Permission();
		permission.setId(1L);
		permission.setAccess(Permission.PUBLIC);

		ProfilePermission pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		ProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/updatepermission", pp,
				ProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Permissão não apagada.");
	}

	private static void deletePermission() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		Profile profile = new Profile();
		profile.setId(3L);
		profile.setName("Perfil para ser apagado");

		Permission permission = new Permission();
		permission.setId(1L);
		permission.setAccess(Permission.READ_ONLY);

		ProfilePermission pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		ProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/deletepermission", pp,
				ProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Permissão não apagada.");
	}

	private static User getUser(String login) throws Exception {

		URL url = new URL(SERVER_URL + "rest/user/" + login);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		User answer = null;
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output, new TypeToken<User>() {
			}.getType());
		}
		conn.disconnect();
		return answer;
	}

	private static void associatePermissions() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = getUser("teste1");

		Profile profile = new Profile();
		profile.setId(1L);
		profile.setName("Perfil somente para leitores");

		Permission permission = new Permission();
		permission.setId(1L);
		permission.setAccess(Permission.READ_ONLY);

		ProfilePermission pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		List<ProfilePermission> ppList = new ArrayList<ProfilePermission>();
		ppList.add(pp);

		profile = new Profile();
		profile.setId(4L);
		profile.setName("Perfil para arquivos abertos ao público");

		permission = new Permission();
		permission.setId(3L);
		permission.setAccess(Permission.PUBLIC);

		pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		ppList.add(pp);

		UserProfilePermission upp = new UserProfilePermission();
		upp.setUser(user);
		upp.setProfilePermissions(ppList);

		UserProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/associatepermission", upp,
				UserProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Erro ao associar permissão.");

		user = getUser("teste2");

		ppList.clear();
		ppList.add(pp);

		profile = new Profile();
		profile.setId(2L);
		profile.setName("Perfil somente para escritores");

		permission = new Permission();
		permission.setId(2L);
		permission.setAccess(Permission.WRITE);

		pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		ppList.add(pp);

		upp = new UserProfilePermission();
		upp.setUser(user);
		upp.setProfilePermissions(ppList);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/associatepermission", upp,
				UserProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Erro ao associar permissão.");

	}

	private static void desassociatePermissions() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = getUser("teste2");

		Profile profile = new Profile();
		profile.setId(4L);
		profile.setName("Perfil para arquivos abertos ao público");

		Permission permission = new Permission();
		permission.setId(3L);
		permission.setAccess(Permission.PUBLIC);

		ProfilePermission pp = new ProfilePermission();
		pp.setPermission(permission);
		pp.setProfile(profile);

		List<ProfilePermission> ppList = new ArrayList<ProfilePermission>();
		ppList.add(pp);

		UserProfilePermission upp = new UserProfilePermission();
		upp.setUser(user);
		upp.setProfilePermissions(ppList);

		UserProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/desassociatepermission", upp,
				UserProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Erro ao desassociar permissão.");

	}

	private static void getAllUserPermissions(User user) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		
		UserProfilePermission[] answer = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/alluserpermissions", user, UserProfilePermission[].class);
		
		System.out.println("Usuários e permissões: ");
		for (UserProfilePermission upp : answer) {

			System.out.println("	" + upp.getUser().getLogin() + " "
					+ upp.getUser().getPassword());
			for (ProfilePermission pp : upp.getProfilePermissions())
				System.out.println("		" + pp.getProfile().getName() + " "
						+ pp.getPermission().getAccess());
		}
	}

	private static void createUsers() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setLogin("teste1");
		user.setName("Teste 1");
		user.setPassword("teste");
		user.setAdmin(false);

		User response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/createuser", user, User.class);

		if (response == null)
			throw new RuntimeException("Usuário não inserido.");

		user = new User();
		user.setLogin("teste2");
		user.setName("Teste 2");
		user.setPassword("teste");
		user.setAdmin(true);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/createuser", user, User.class);

		if (response == null)
			throw new RuntimeException("Usuário não inserido.");

		user = new User();
		user.setLogin("teste3");
		user.setName("Teste para ser apagado");
		user.setPassword("teste");
		user.setAdmin(false);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/createuser", user, User.class);

		if (response == null)
			throw new RuntimeException("Usuário não inserido.");

	}

	private static void updateUser() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = getUser("teste3");
		user.setName("Teste a ser apagado");
		user.setAdmin(true);

		User response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/updateuser", user, User.class);

		if (response == null)
			throw new RuntimeException("Usuário não atualizado.");

	}

	private static void getAllUsers(User user) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
				
		User[] answer = restTemplate.postForObject(SERVER_URL
				+ "rest/allusers", user, User[].class);
		
		System.out.println("Usuários: ");
		for (User u : answer) {
			System.out.println("	" + "Login: " + u.getLogin() + " Nome: "
					+ u.getName() + " Senha: " + u.getPassword() + " Admin: "
					+ (u.isAdmin() ? "Sim" : "Não"));
		}

	}

	private static void deleteUser() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setLogin("teste3");
		user.setName("Teste a ser apagado");
		user.setPassword("teste");
		user.setAdmin(true);

		User response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/deleteuser", user, User.class);

		if (response == null)
			throw new RuntimeException("Usuário não apagado.");
	}

	private static void requestAssociation() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		List<ProfilePermission> list = getAllPermissions();
		List<ProfilePermission> requestPermission = new ArrayList<ProfilePermission>();
		for (ProfilePermission pp : list) {
			if (pp.getPermission().getAccess().equals(Permission.WRITE)) {
				requestPermission.add(pp);
				break;
			}
		}

		List<ProfilePermission> response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/requestassociation" + "/teste1",
				requestPermission, ArrayList.class);

		if (response == null)
			throw new RuntimeException("Usuário não apagado.");
	}

	private static void requestDesassociation() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		List<ProfilePermission> list = getAllPermissions();
		List<ProfilePermission> requestPermission = new ArrayList<ProfilePermission>();
		for (ProfilePermission pp : list) {
			if (pp.getPermission().getAccess().equals(Permission.WRITE)) {
				requestPermission.add(pp);
				break;
			}
		}

		List<ProfilePermission> response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/requestdesassociation" + "/teste1",
				requestPermission, ArrayList.class);

		if (response == null)
			throw new RuntimeException("Usuário não apagado.");
	}

	private static void getAllRequestedAssociations() throws Exception {

		URL url = new URL(SERVER_URL + "rest/allrequestedassociations");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		Map<String, List<ProfilePermission>> answer = new HashMap<String, List<ProfilePermission>>();
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output,
					new TypeToken<Map<String, List<ProfilePermission>>>() {
					}.getType());
		}

		conn.disconnect();
		System.out.println("Usuários requisitantes de associações: ");
		for (String u : answer.keySet()) {
			System.out.println("	" + "Login: " + u);
			List<ProfilePermission> list = answer.get(u);
			System.out.println("	Perfils: ");
			for (ProfilePermission pp : list) {
				System.out.println("		" + pp.getProfile().getName());
			}
		}

	}

	private static void getAllRequestedDesassociations() throws Exception {

		URL url = new URL(SERVER_URL + "rest/allrequesteddesassociations");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		Map<String, List<ProfilePermission>> answer = new HashMap<String, List<ProfilePermission>>();
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output,
					new TypeToken<Map<String, List<ProfilePermission>>>() {
					}.getType());
		}

		conn.disconnect();
		System.out.println("Usuários requisitantes de desassociações: ");
		for (String u : answer.keySet()) {
			System.out.println("	" + "Login: " + u);
			List<ProfilePermission> list = answer.get(u);
			System.out.println("	Perfils: ");
			for (ProfilePermission pp : list) {
				System.out.println("		" + pp.getProfile().getName());
			}
		}

	}

	private static List<ProfilePermission> getSpecificPermissionToAssociate(
			String login) throws Exception {

		URL url = new URL(SERVER_URL + "rest/requestedassociation/" + login);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output,
					new TypeToken<List<ProfilePermission>>() {
					}.getType());
		}

		conn.disconnect();

		return answer;

	}

	private static List<ProfilePermission> getSpecificPermissionToDesassociate(
			String login) throws Exception {

		URL url = new URL(SERVER_URL + "rest/requesteddesassociation/" + login);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falha na conexão : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		while ((output = br.readLine()) != null) {
			Gson gson = new Gson();
			answer = gson.fromJson(output,
					new TypeToken<List<ProfilePermission>>() {
					}.getType());
		}

		conn.disconnect();

		return answer;

	}

	private static void associateSpecificPermission(String login)
			throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		
		List<ProfilePermission> pp = getSpecificPermissionToAssociate(login);
		
		User user = getUser(login);

		UserProfilePermission upp = new UserProfilePermission();
		upp.setProfilePermissions(pp);
		upp.setUser(user);		
		UserProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/associatepermission", upp,
				UserProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Erro ao associar permissão.");

	}

	private static void desassociateSpecificPermission(String login)
			throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
		
		List<ProfilePermission> pp = getSpecificPermissionToDesassociate(login);
		
		User user = getUser(login);

		UserProfilePermission upp = new UserProfilePermission();
		upp.setProfilePermissions(pp);
		upp.setUser(user);

		UserProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/desassociatepermission", upp,
				UserProfilePermission.class);

		if (response == null)
			throw new RuntimeException("Erro ao desassociar permissão.");

	}

}
