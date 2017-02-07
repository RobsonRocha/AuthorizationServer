package br.com.example.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.example.model.Permission;
import br.com.example.model.Profile;
import br.com.example.model.ProfilePermission;
import br.com.example.model.User;
import br.com.example.model.UserProfilePermission;

public class MainTest {
	public static final String SERVER_URL = "http://localhost:8080/AuthorizationServer/";

	private User admin;
	private List<User> users = new ArrayList<User>();

	@BeforeClass
	public void createAdmin() {
		RestTemplate restTemplate = new RestTemplate();

		admin = restTemplate.getForObject(
				"http://localhost:8080/AuthorizationServer/"
						+ "rest/user/createadmin", User.class);

		Assert.assertTrue(admin != null);
	}

	@Test
	private User login(String login, String password) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User(login, password);

		User response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/login", user, User.class);

		if (response == null)
			throw new RuntimeException("Login e/ou senha inválidos.");

		return response;
	}

	@BeforeClass
	private void createPermission() {
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

		Assert.assertTrue(response != null);

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

		Assert.assertTrue(response != null);

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

		Assert.assertTrue(response != null);

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

		Assert.assertTrue(response != null);

	}

	@Test
	private List<ProfilePermission> getAllPermissions() {
		RestTemplate restTemplate = new RestTemplate();

		ProfilePermission[] pp = restTemplate.getForObject(SERVER_URL
				+ "rest/permission/allpermissions", ProfilePermission[].class);

		Assert.assertTrue(pp.length > 0);

		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		answer.addAll(Arrays.asList(pp));

		System.out.println("Perfils: ");
		for (ProfilePermission ppr : answer) {

			System.out.println("	Nome: " + ppr.getProfile().getName()
					+ " Permissão: " + ppr.getPermission().getAccess());
		}

		return answer;

	}

	@Test
	private void updatePermission() throws Exception {
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

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json; charset=utf-8");
		Gson gson = new Gson();
		String json = gson.toJson(pp);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> re = restTemplate.exchange(SERVER_URL
				+ "rest/permission/updatepermission", HttpMethod.PUT, entity,
				String.class);

		Assert.assertTrue(re.getStatusCode() == HttpStatus.OK);

	}

	@Test
	private void deletePermission() throws Exception {
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

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		Gson gson = new Gson();
		String json = gson.toJson(pp);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> re =	restTemplate.exchange(SERVER_URL + "rest/permission/deletepermission",
				HttpMethod.DELETE, entity, String.class);

		Assert.assertTrue(re.getStatusCode() == HttpStatus.OK);
	}

	@Test
	private void getUser() {
		RestTemplate restTemplate = new RestTemplate();

		User response = restTemplate.getForObject(SERVER_URL + "rest/user/"
				+ "admin", User.class);

		Assert.assertTrue(response != null);

	}

	@Test
	private void getUserThatDoesNotExistTest() {
		RestTemplate restTemplate = new RestTemplate();

		User response = restTemplate.getForObject(SERVER_URL + "rest/user/"
				+ "admin111", User.class);

		Assert.assertTrue(response == null);

	}

	private User getUser(String login) {
		RestTemplate restTemplate = new RestTemplate();

		User response = restTemplate.getForObject(SERVER_URL + "rest/user/"
				+ login, User.class);

		return response;
	}

	@Test
	private void associatePermissions() {
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

		Assert.assertTrue(response != null);

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

		Assert.assertTrue(response != null);

	}

	@Test
	private void desassociatePermissions() throws Exception {
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

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json; charset=utf-8");
		Gson gson = new Gson();
		String json = gson.toJson(upp);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> re = restTemplate.exchange(SERVER_URL
				+ "rest/permission/desassociatepermission", HttpMethod.DELETE,
				entity, String.class);

		Assert.assertTrue(re.getStatusCode() == HttpStatus.OK);

	}

	@Test
	private void getAllUserPermissionsFromAdmin() {
		RestTemplate restTemplate = new RestTemplate();

		UserProfilePermission[] answer = restTemplate.getForObject(SERVER_URL
				+ "rest/permission/alluserpermissions/" + "admin",
				UserProfilePermission[].class);

		Assert.assertTrue(answer.length > 1);

		System.out.println("Usuários e permissões: ");
		for (UserProfilePermission upp : answer) {

			System.out.println("	" + upp.getUser().getLogin() + " "
					+ upp.getUser().getPassword());
			for (ProfilePermission pp : upp.getProfilePermissions())
				System.out.println("		" + pp.getProfile().getName() + " "
						+ pp.getPermission().getAccess());
		}
	}

	@Test
	private void getAllUserPermissionsFromNotAdmin() {
		RestTemplate restTemplate = new RestTemplate();

		String user = "teste1";

		UserProfilePermission[] answer = restTemplate.getForObject(SERVER_URL
				+ "rest/permission/alluserpermissions/" + user,
				UserProfilePermission[].class);

		System.out.println("Usuários e permissões: ");
		for (UserProfilePermission upp : answer) {

			Assert.assertTrue(user.equals(upp.getUser().getLogin()));

			System.out.println("	" + upp.getUser().getLogin() + " "
					+ upp.getUser().getPassword());
			for (ProfilePermission pp : upp.getProfilePermissions())
				System.out.println("		" + pp.getProfile().getName() + " "
						+ pp.getPermission().getAccess());
		}
	}

	@BeforeClass
	private void createUsers() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setLogin("teste1");
		user.setName("Teste 1");
		user.setPassword("teste");
		user.setAdmin(false);

		User response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/createuser", user, User.class);

		Assert.assertTrue(response != null);
		users.add(response);

		user = new User();
		user.setLogin("teste2");
		user.setName("Teste 2");
		user.setPassword("teste");
		user.setAdmin(true);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/createuser", user, User.class);

		Assert.assertTrue(response != null);
		users.add(response);

		user = new User();
		user.setLogin("teste3");
		user.setName("Teste para ser apagado");
		user.setPassword("teste");
		user.setAdmin(false);

		response = restTemplate.postForObject(SERVER_URL
				+ "rest/user/createuser", user, User.class);

		Assert.assertTrue(response != null);
		users.add(response);

	}

	@Test
	private void updateUser() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = getUser("teste1");
		user.setName("Teste 1 modificado");
		user.setAdmin(false);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		Gson gson = new Gson();
		String json = gson.toJson(user);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> re = restTemplate.exchange(SERVER_URL
				+ "rest/user/updateuser", HttpMethod.PUT, entity, String.class);

		Assert.assertTrue(re.getStatusCode() == HttpStatus.OK);
		
		User u = getUser("teste1");
		
		Assert.assertEquals(u.getName(), user.getName());
	}

	@Test
	private void getAllUsers() {
		RestTemplate restTemplate = new RestTemplate();

		User[] answer = restTemplate.getForObject(SERVER_URL + "rest/allusers/"
				+ "admin", User[].class);

		Assert.assertTrue(answer.length > 0);

		System.out.println("Usuários: ");
		for (User u : answer) {
			System.out.println("	" + "Login: " + u.getLogin() + " Nome: "
					+ u.getName() + " Senha: " + u.getPassword() + " Admin: "
					+ (u.isAdmin() ? "Sim" : "Não"));
		}

	}
	
	@Test
	private void getAllUsersFromNotAdmin() {
		RestTemplate restTemplate = new RestTemplate();

		User[] answer = restTemplate.getForObject(SERVER_URL + "rest/allusers/"
				+ "teste1", User[].class);

		Assert.assertTrue(answer.length == 1);

		System.out.println("Usuários: ");
		for (User u : answer) {
			System.out.println("	" + "Login: " + u.getLogin() + " Nome: "
					+ u.getName() + " Senha: " + u.getPassword() + " Admin: "
					+ (u.isAdmin() ? "Sim" : "Não"));
		}

	}

	@Test
	private void deleteUser() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setLogin("teste3");
		user.setName("Teste 3 modificado");
		user.setPassword("teste");
		user.setAdmin(true);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		Gson gson = new Gson();
		String json = gson.toJson(user);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> re = restTemplate.exchange(SERVER_URL + "rest/user/deleteuser",
				HttpMethod.DELETE, entity, String.class);

		Assert.assertTrue(re.getStatusCode() == HttpStatus.OK);
		
		User u = getUser(user.getLogin());
		Assert.assertTrue(u == null);
	}

	@Test
	private void requestAssociation(){
		RestTemplate restTemplate = new RestTemplate();

		List<ProfilePermission> list = getAllPermissions();
		List<ProfilePermission> requestPermission = new ArrayList<ProfilePermission>();
		for (ProfilePermission pp : list) {
			if (pp.getPermission().getAccess().equals(Permission.WRITE)) {
				requestPermission.add(pp);
				break;
			}
		}

		List<ProfilePermission> response = restTemplate.postForObject(
				SERVER_URL + "rest/user/requestassociation" + "/teste1",
				requestPermission, ArrayList.class);

		Assert.assertTrue(response != null);
		Assert.assertTrue(!response.isEmpty());
	}

	@Test
	private void requestDesassociation(){
		RestTemplate restTemplate = new RestTemplate();

		List<ProfilePermission> list = getAllPermissions();
		List<ProfilePermission> requestPermission = new ArrayList<ProfilePermission>();
		for (ProfilePermission pp : list) {
			if (pp.getPermission().getAccess().equals(Permission.WRITE)) {
				requestPermission.add(pp);
				break;
			}
		}

		List<ProfilePermission> response = restTemplate.postForObject(
				SERVER_URL + "rest/user/requestdesassociation" + "/teste1",
				requestPermission, ArrayList.class);

		Assert.assertTrue(response != null);
		Assert.assertTrue(!response.isEmpty());
	}

	@Test
	private void getAllRequestedAssociations() throws Exception {
		
		requestAssociation();
		
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
		
		Assert.assertTrue(answer != null);
		Assert.assertTrue(!answer.isEmpty());

	}

	@Test
	private void getAllRequestedDesassociations() throws Exception {
		
		requestDesassociation();
		
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
		
		Assert.assertTrue(answer != null);
		Assert.assertTrue(!answer.isEmpty());

	}

	@Test
	private void getSpecificPermissionToAssociate() {
		
		requestAssociation();
		
		String login = "teste1";
		RestTemplate restTemplate = new RestTemplate();

		ProfilePermission[] pp = restTemplate.getForObject(SERVER_URL
				+ "rest/requestedassociation/" + login,
				ProfilePermission[].class);

		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		answer.addAll(Arrays.asList(pp));

		Assert.assertTrue(answer != null);
		Assert.assertTrue(!answer.isEmpty());

	}

	private List<ProfilePermission> getSpecificPermissionToAssociate(
			String login){

		RestTemplate restTemplate = new RestTemplate();

		ProfilePermission[] pp = restTemplate.getForObject(SERVER_URL
				+ "rest/requestedassociation/" + login,
				ProfilePermission[].class);

		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		answer.addAll(Arrays.asList(pp));

		return answer;

	}

	@Test
	private void getSpecificPermissionToDesassociate() {
		
		requestDesassociation();
		
		RestTemplate restTemplate = new RestTemplate();

		ProfilePermission[] pp = restTemplate.getForObject(SERVER_URL
				+ "rest/requesteddesassociation/" + "teste1",
				ProfilePermission[].class);

		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		answer.addAll(Arrays.asList(pp));

		Assert.assertTrue(answer != null);
		Assert.assertTrue(!answer.isEmpty());

	}

	private List<ProfilePermission> getSpecificPermissionToDesassociate(
			String login) {
		RestTemplate restTemplate = new RestTemplate();

		ProfilePermission[] pp = restTemplate.getForObject(SERVER_URL
				+ "rest/requesteddesassociation/" + login,
				ProfilePermission[].class);

		List<ProfilePermission> answer = new ArrayList<ProfilePermission>();
		answer.addAll(Arrays.asList(pp));

		return answer;

	}

	@Test
	private void associateSpecificPermission() throws Exception {
		requestAssociation();
		RestTemplate restTemplate = new RestTemplate();

		List<ProfilePermission> pp = getSpecificPermissionToAssociate("teste1");

		User user = getUser("teste1");

		UserProfilePermission upp = new UserProfilePermission();
		upp.setProfilePermissions(pp);
		upp.setUser(user);
		UserProfilePermission response = restTemplate.postForObject(SERVER_URL
				+ "rest/permission/associatepermission", upp,
				UserProfilePermission.class);

		Assert.assertTrue(response != null);

	}

	@Test
	private void desassociateSpecificPermission() {
		requestDesassociation();
		
		String login = "teste1";

		RestTemplate restTemplate = new RestTemplate();

		List<ProfilePermission> pp = getSpecificPermissionToDesassociate(login);

		User user = getUser(login);

		UserProfilePermission upp = new UserProfilePermission();
		upp.setProfilePermissions(pp);
		upp.setUser(user);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json; charset=utf-8");
		Gson gson = new Gson();
		String json = gson.toJson(upp);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);		

		ResponseEntity<String> re = restTemplate.exchange(SERVER_URL
				+ "rest/permission/desassociatepermission", HttpMethod.DELETE,
				entity, String.class);
		Assert.assertTrue(re.getStatusCode() == HttpStatus.OK);
	}

}
