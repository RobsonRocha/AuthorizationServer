package br.com.alis.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.alis.model.Permission;
import br.com.alis.model.ProfilePermission;
import br.com.alis.model.User;
import br.com.alis.model.UserProfilePermission;
import br.com.alis.utils.Utils;

@Controller
public class MainController {

	private static final Logger logger = LoggerFactory
			.getLogger(MainController.class);

	//Tipos de permissão.
	private Permission arrayPermissions[] = {
			new Permission(1L, Permission.READ_ONLY),
			new Permission(2L, Permission.WRITE),
			new Permission(3L, Permission.PUBLIC) };
	
	//Perfis e suas permissões cadastradas.
	Set<ProfilePermission> permissions = new HashSet<ProfilePermission>();
	//Um mapa que contém uma espécie de consulta, trazer as permissões por usuário (um usuário tem uma lista de permissões.
	Map<String, UserProfilePermission> userPermissions = new HashMap<String, UserProfilePermission>();

	Map<String, User> users = new HashMap<String, User>();
	
	//Requisições de associações por usuários
	Map<String, List<ProfilePermission>> requestedAssociations = new HashMap<String, List<ProfilePermission>>();
	//Requisições de desassociações por usuários
	Map<String, List<ProfilePermission>> requestedDesassociations = new HashMap<String, List<ProfilePermission>>();

	//Busca todas as permissões
	@RequestMapping(value = "/rest/permission/allpermissions", method = RequestMethod.GET)
	public @ResponseBody List<ProfilePermission> getAllPermissions() {
		logger.info("Listando todas as permissões.");
		List<ProfilePermission> result = new ArrayList<ProfilePermission>(
				this.permissions);
		return result;
	}
	
	//Busca todos os tipos de permissões, usado para montar combobox.
	@RequestMapping(value = "/rest/permission/allaccesspermissions", method = RequestMethod.GET)
	public @ResponseBody List<Permission> getAllAccessPermissions() {
		logger.info("Listando todos os tipos de permissões.");
		List<Permission> result = new ArrayList<Permission>(
				Arrays.asList(this.arrayPermissions));
		return result;
	}
	
	//Busca todas as permissões de um usuário, se o usuário for administrador, busca todos sem filtrar.
	@RequestMapping(value = "/rest/permission/alluserpermissions", method = RequestMethod.POST)
	public @ResponseBody List<UserProfilePermission> getUserProfilePermissions(
			@RequestBody User loggedUser) {
		logger.info("Listando todas as permissões associadas.");
		List<UserProfilePermission> result = new ArrayList<UserProfilePermission>();
		if (loggedUser.isAdmin())
			result = new ArrayList<UserProfilePermission>(
					this.userPermissions.values());
		else {
			result = new ArrayList<UserProfilePermission>();
			result.add(this.userPermissions.get(loggedUser.getLogin()));
		}
		return result;
	}

	//Busca uma permissão específica
	@RequestMapping(value = "/rest/permission/{id}", method = RequestMethod.GET)
	public @ResponseBody ProfilePermission getPermission(
			@PathVariable("id") Long id) {
		logger.info("Buscando permissão.");
		for (ProfilePermission pp : this.permissions)
			if (pp.getProfile().getId() == id)
				return pp;
		return null;
	}

	//Cria uma permissão, se o id for nulo, utiliza-se uma espécie de sequence
	@RequestMapping(value = "/rest/permission/createpermission", method = RequestMethod.POST)
	public @ResponseBody ProfilePermission createPermission(
			@RequestBody ProfilePermission pp) {
		logger.info("Criando uma nova permissão.");
		if (!this.permissions.isEmpty() && pp.getProfile().getId() == null) {
			Long id = 0L;
			for (ProfilePermission ppId : this.permissions) {
				if (ppId.getProfile().getId() > id)
					id = ppId.getProfile().getId();
			}
			pp.getProfile().setId(id + 1L);
		} else if (this.permissions.isEmpty()
				&& pp.getProfile().getId() == null)
			pp.getProfile().setId(1L);

		this.permissions.add(pp);
		return pp;

	}

	
	//Atualiza permissão e todos as estruturas que a contém.
	@RequestMapping(value = "/rest/permission/updatepermission", method = RequestMethod.POST)
	public @ResponseBody ProfilePermission updatePermission(
			@RequestBody ProfilePermission pp) {
		logger.info("Atualizando uma nova permissão.");
		if (this.permissions.contains(pp)) {
			for (ProfilePermission pro : this.permissions) {
				if (pro.equals(pp)) {
					this.permissions.remove(pp);
					this.permissions.add(pp);

					for (String user : this.userPermissions.keySet()) {
						UserProfilePermission upp = this.userPermissions
								.get(user);
						List<ProfilePermission> ppList = new ArrayList<ProfilePermission>(upp.getProfilePermissions());
						for (ProfilePermission ppUpdate : ppList) {
							if (ppUpdate.equals(pp)) {
								upp.getProfilePermissions().remove(ppUpdate);
								upp.getProfilePermissions().add(pp);
							}
						}
					}

					break;
				}
			}
		}
		return pp;

	}
	
	
	//Apaga uma permissão e faz uma espécie de delete cascade.
	@RequestMapping(value = "/rest/permission/deletepermission", method = RequestMethod.POST)
	public @ResponseBody ProfilePermission deletePermission(
			@RequestBody ProfilePermission pp) {
		logger.info("Apagando uma permissão.");
		boolean deleted = this.permissions.remove(pp);
		if (deleted) {
			List<String> users = new ArrayList<String>(
					this.userPermissions.keySet());
			for (String user : users) {
				UserProfilePermission upp = this.userPermissions.get(user);
				List<ProfilePermission> ppList = new ArrayList<ProfilePermission>(
						upp.getProfilePermissions());
				for (ProfilePermission ppDelete : ppList) {
					if (ppDelete.equals(pp)) {
						UserProfilePermission usrPr = new UserProfilePermission();
						usrPr.setUser(upp.getUser());
						usrPr.getProfilePermissions().add(ppDelete);
						desassociatePermission(usrPr);
					}
				}
			}
			
			users = new ArrayList<String>(this.requestedAssociations.keySet());
			for (String user : users) {
				List<ProfilePermission> pps = this.requestedAssociations.get(user);
				List<ProfilePermission> ppList = new ArrayList<ProfilePermission>(
						this.requestedAssociations.get(user));
				for (ProfilePermission ppDelete : ppList) {
					if (ppDelete.equals(pp)) {
						pps.remove(pp);
						if(pps.isEmpty())
							this.requestedAssociations.remove(user);
						else
							this.requestedAssociations.put(user, pps);
					}
				}
			}
			
			users = new ArrayList<String>(this.requestedDesassociations.keySet());
			for (String user : users) {
				List<ProfilePermission> pps = this.requestedDesassociations.get(user);
				List<ProfilePermission> ppList = new ArrayList<ProfilePermission>(
						this.requestedDesassociations.get(user));
				for (ProfilePermission ppDelete : ppList) {
					if (ppDelete.equals(pp)) {
						pps.remove(pp);
						if(pps.isEmpty())
							this.requestedDesassociations.remove(user);
						else
							this.requestedDesassociations.put(user, pps);
					}
				}
			}
		}

		return pp;

	}
	
	
	//Associa a permissão ao usuário
	@RequestMapping(value = "/rest/permission/associatepermission", method = RequestMethod.POST)
	public @ResponseBody UserProfilePermission associatePermission(
			@RequestBody UserProfilePermission up) {
		logger.info("Associando uma permissão a um usuário.");

		UserProfilePermission upp = this.userPermissions.get(up.getUser()
				.getLogin());
		boolean associate = false;
		if (upp == null) {
			this.userPermissions.put(up.getUser().getLogin(), up);
			associate = true;
		} else {
			for (ProfilePermission pp : up.getProfilePermissions()) {
				if (!upp.getProfilePermissions().contains(pp)) {
					upp.getProfilePermissions().add(pp);
					this.userPermissions.put(up.getUser().getLogin(), upp);
					associate = true;
				}
			}
		}

		List<ProfilePermission> list = this.requestedAssociations.get(up
				.getUser().getLogin());
		if (list != null && associate) {
			List<ProfilePermission> listCopy = new ArrayList<ProfilePermission>(
					list);
			for (ProfilePermission pp : list) {
				for (ProfilePermission ppDelete : up.getProfilePermissions()) {
					if (pp.equals(ppDelete)) {
						listCopy.remove(ppDelete);
						if (listCopy.isEmpty())
							this.requestedAssociations.remove(up.getUser()
									.getLogin());
						else
							this.requestedAssociations.put(up.getUser()
									.getLogin(), listCopy);
					}

				}

			}
		}
		return up;

	}

	//Desassocia a permissão do usuário
	@RequestMapping(value = "/rest/permission/desassociatepermission", method = RequestMethod.POST)
	public @ResponseBody UserProfilePermission desassociatePermission(
			@RequestBody UserProfilePermission up) {
		try {
			logger.info("Desassociando uma permissão a um usuário.");

			UserProfilePermission upp = this.userPermissions.get(up.getUser()
					.getLogin());
			boolean desassociate = false;

			if (upp != null) {
				for (ProfilePermission pp : up.getProfilePermissions()) {
					upp.getProfilePermissions().remove(pp);
					if (upp.getProfilePermissions().isEmpty())
						this.userPermissions.remove(up.getUser().getLogin());
					else
						this.userPermissions.put(up.getUser().getLogin(), upp);
					desassociate = true;
				}
			}

			List<ProfilePermission> list = this.requestedDesassociations.get(up
					.getUser().getLogin());

			if (list != null && desassociate) {
				List<ProfilePermission> listCopy = new ArrayList<ProfilePermission>(
						list);
				for (ProfilePermission pp : list) {
					for (ProfilePermission ppDelete : up
							.getProfilePermissions()) {
						if (pp.equals(ppDelete)) {
							listCopy.remove(ppDelete);
							if (listCopy.isEmpty())
								this.requestedDesassociations.remove(up
										.getUser().getLogin());
							else
								this.requestedDesassociations.put(up.getUser()
										.getLogin(), listCopy);
						}

					}

				}
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return up;

	}
	
	
	@RequestMapping(value = "/rest/user/login", method = RequestMethod.POST)
	public @ResponseBody User login(@RequestBody User user) {
		logger.info("Tentando logar.");
		User usr = this.users.get(user.getLogin());
		User loggedUser = null;
		if (usr != null) {
			if (usr.getPassword().equals(
					Utils.convertStringToMd5(user.getPassword())))
				loggedUser = usr;
		}
		return loggedUser;
	}

	// Cria um usuário administrador, usado somente no teste
	@RequestMapping(value = "/rest/user/createadmin", method = RequestMethod.GET)
	public @ResponseBody User createAdmin() {
		logger.info("Criando admin para teste.");
		User user = new User();
		user.setAdmin(true);
		user.setLogin("admin");
		user.setName("Administrador");
		user.setPassword(Utils.convertStringToMd5("admin"));
		users.put("admin", user);

		return user;
	}

	
	@RequestMapping(value = "/rest/user/{login}", method = RequestMethod.GET)
	public @ResponseBody User getUser(@PathVariable("login") String login) {
		logger.info("Buscando usuário.");
		return this.users.get(login);
	}

	@RequestMapping(value = "/rest/allusers", method = RequestMethod.POST)
	public @ResponseBody List<User> getAllUsers(@RequestBody User loggedUser) {
		logger.info("Buscando todos os usuários.");
		if (loggedUser.isAdmin()) {
			List<User> result = new ArrayList<User>(this.users.values());
			return result;
		} else {
			List<User> result = new ArrayList<User>();
			result.add(this.users.get(loggedUser.getLogin()));
			return result;
		}
	}

	@RequestMapping(value = "/rest/user/createuser", method = RequestMethod.POST)
	public @ResponseBody User createUser(@RequestBody User user) {
		logger.info("Criando usuário.");
		User usr = this.users.get(user.getLogin());
		if (usr == null) {
			user.setPassword(Utils.convertStringToMd5(user.getPassword()));
			this.users.put(user.getLogin(), user);
		}
		return user;

	}
	
	
	//Atualiza o usuário e todas suas permissões
	@RequestMapping(value = "/rest/user/updateuser", method = RequestMethod.POST)
	public @ResponseBody User updateUser(@RequestBody User user) {
		logger.info("Atualizando usuário.");
		User usr = this.users.get(user.getLogin());
		if (usr != null) {
			this.users.put(user.getLogin(), user);
			for (String login : this.userPermissions.keySet()) {
				if (login.equals(user.getLogin())) {
					UserProfilePermission upp = this.userPermissions.get(login);
					upp.setUser(user);
					this.userPermissions.put(login, upp);
				}
			}
		}
		return user;

	}

	//Apaga o usuário e todas as suas permissões e requisições
	@RequestMapping(value = "/rest/user/deleteuser", method = RequestMethod.POST)
	public @ResponseBody User deleteUser(@RequestBody User user) {
		logger.info("Apagando usuário.");
		this.users.remove(user.getLogin());

		this.userPermissions.remove(user.getLogin());
		this.requestedAssociations.remove(user.getLogin());
		this.requestedDesassociations.remove(user.getLogin());

		return user;
	}

	@RequestMapping(value = "/rest/user/changepassword", method = RequestMethod.POST)
	public @ResponseBody User changePassword(@RequestBody User user) {
		logger.info("Alterando senha.");
		User usr = this.users.get(user.getLogin());
		if (usr != null) {
			usr.setPassword(Utils.convertStringToMd5(user.getPassword()));
			this.users.put(user.getLogin(), usr);
		}
		return user;
	}
	
	//Requisita um acesso ao usuário.
	@RequestMapping(value = "/rest/user/requestassociation/{login}", method = RequestMethod.POST)
	public @ResponseBody List<ProfilePermission> createRequestAssociation(
			@PathVariable("login") String login,
			@RequestBody List<ProfilePermission> pp) {
		logger.info("Solicitando associação.");

		List<ProfilePermission> requests = this.requestedAssociations
				.get(login);

		if (requests == null) {
			requests = new ArrayList<ProfilePermission>();
			requests.addAll(pp);
			this.requestedAssociations.put(login, requests);
		} else {
			requests.addAll(pp);
			this.requestedAssociations.put(login, requests);
		}

		return pp;

	}
	
	
	//Apaga uma solicitação
	@RequestMapping(value = "/rest/user/deleterequestassociation/{login}", method = RequestMethod.POST)
	public @ResponseBody ProfilePermission deleteRequestAssociation(
			@PathVariable("login") String login,
			@RequestBody ProfilePermission pp) {
		logger.info("Apagando solicitação.");

		List<ProfilePermission> requests = this.requestedAssociations
				.get(login);

		if (requests != null) {
			requests.remove(pp);
			if(requests.isEmpty())
				this.requestedAssociations.remove(login);
			else
				this.requestedAssociations.put(login, requests);
		}

		return pp;

	}

	//Requisita uma desassociação
	@RequestMapping(value = "/rest/user/requestdesassociation/{login}", method = RequestMethod.POST)
	public @ResponseBody List<ProfilePermission> createRequestDesassociation(
			@PathVariable("login") String login,
			@RequestBody List<ProfilePermission> pp) {
		logger.info("Solicitando desassociação.");

		List<ProfilePermission> requests = this.requestedDesassociations
				.get(login);

		if (requests == null) {
			requests = new ArrayList<ProfilePermission>();
			requests.addAll(pp);
			this.requestedDesassociations.put(login, requests);
		} else {
			requests.addAll(pp);
			this.requestedDesassociations.put(login, requests);
		}

		return pp;

	}
	
	@RequestMapping(value = "/rest/user/deleterequestdesassociation/{login}", method = RequestMethod.POST)
	public @ResponseBody ProfilePermission deleteRequestDesassociation(
			@PathVariable("login") String login,
			@RequestBody ProfilePermission pp) {
		logger.info("Solicitando associação.");

		List<ProfilePermission> requests = this.requestedDesassociations
				.get(login);

		if (requests != null) {
			requests.remove(pp);
			if(requests.isEmpty())
				this.requestedDesassociations.remove(login);
			else
				this.requestedDesassociations.put(login, requests);
		}

		return pp;

	}

	//Busca todas as solicitações
	@RequestMapping(value = "/rest/allrequestedassociations", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<ProfilePermission>> getAllRequestedAssociations() {
		logger.info("Buscando todas as associações requisitadas.");
		return this.requestedAssociations;
	}

	@RequestMapping(value = "/rest/allrequesteddesassociations", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<ProfilePermission>> getAllRequestedDesassociations() {
		logger.info("Buscando todas as desassociações requisitadas.");
		return this.requestedDesassociations;
	}

	//Busca solicitações específicas do usuário
	@RequestMapping(value = "/rest/requestedassociation/{login}", method = RequestMethod.GET)
	public @ResponseBody List<ProfilePermission> getRequestedAssociation(
			@PathVariable("login") String login) {
		logger.info("Buscando associação.");
		return this.requestedAssociations.get(login);
	}

	@RequestMapping(value = "/rest/requesteddesassociation/{login}", method = RequestMethod.GET)
	public @ResponseBody List<ProfilePermission> getRequestedDesassociation(
			@PathVariable("login") String login) {
		logger.info("Buscando desassociação.");
		return this.requestedDesassociations.get(login);
	}

}
