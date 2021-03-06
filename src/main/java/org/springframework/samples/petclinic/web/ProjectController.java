package org.springframework.samples.petclinic.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Belongs;
import org.springframework.samples.petclinic.model.Department;
import org.springframework.samples.petclinic.model.Project;
import org.springframework.samples.petclinic.enums.Role;
import org.springframework.samples.petclinic.model.UserTW;
import org.springframework.samples.petclinic.service.BelongsService;
import org.springframework.samples.petclinic.service.DepartmentService;
import org.springframework.samples.petclinic.service.ParticipationService;
import org.springframework.samples.petclinic.service.ProjectService;
import org.springframework.samples.petclinic.service.UserTWService;
import org.springframework.samples.petclinic.validation.ProjectValidator;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class ProjectController {
	private final DepartmentService departmentService;
	private final ProjectService projectService;
	private final ParticipationService participationService;
	private final UserTWService userTWService;
	private final BelongsService belongsService;
	private final ProjectValidator projectValidator;

	@Autowired
	public ProjectController(DepartmentService departmentService, ProjectService projectService,
			ParticipationService participationService, UserTWService userTWService, BelongsService belongsService,
			ProjectValidator projectValidator) {
		this.departmentService = departmentService;
		this.projectService = projectService;
		this.participationService = participationService;
		this.userTWService = userTWService;
		this.belongsService = belongsService;
		this.projectValidator = projectValidator;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping(value = "/api/projects")
	public ResponseEntity<List<Project>> getProjects(@RequestParam(required = true) Integer departmentId,
			HttpServletRequest r) {
		Integer userId = (Integer) r.getSession().getAttribute("userId");
		UserTW user = userTWService.findUserById(userId);
		Belongs currentBelongs = belongsService.findCurrentBelongs(userId, departmentId);
		// Revisa si perteneces TODO this isnt even needed
		log.info("Comprobando si el usuario con id "+userId+" perteneces al departamento o es team owner");
		if (user.getRole().equals(Role.team_owner) || currentBelongs != null) {
			List<Project> l = new ArrayList<>();
			l = departmentService.findDepartmentById(departmentId).getProjects();
			log.info("Obteniendo proyectos del departamento con id: "+departmentId);
			return ResponseEntity.ok(l);
		} else {
			log.error("El usuario no tiene permisos");
			return ResponseEntity.status(403).build();
		}

	}

	@GetMapping(value = "/api/project/details")
	public ResponseEntity<Map<String, Object>> getProject(@RequestParam(required = true) Integer projectId,
			HttpServletRequest r) {
		Integer userId = (Integer) r.getSession().getAttribute("userId");
		UserTW user = userTWService.findUserById(userId);
		Project project = projectService.findProjectById(projectId);
		Belongs currentBelongs = belongsService.findCurrentBelongs(userId, project.getDepartment().getId());
		if (user.getRole().equals(Role.team_owner) || currentBelongs != null) {
			List<UserTW> users = projectService.findUserProjects(project.getId()).stream().collect(Collectors.toList());
			Map<String, Object> m = new HashMap<>();
			m.put("members", users);
			m.put("milestones", project.getMilestones());
			m.put("tags", project.getTags());
			return ResponseEntity.ok(m);
		} else {
			return ResponseEntity.status(403).build();
		}

	}

	@GetMapping(value = "/api/projects/mine")
	public List<Project> getMyProjects(HttpServletRequest r, @RequestParam(required = true) Integer departmentId) {

		List<Project> l = new ArrayList<>();
		Integer userId = (Integer) r.getSession().getAttribute("userId");
		log.info("Obteniendo los departamentos del usuario con id: "+userId);
		l = participationService.findMyDepartmentProjects(userId, departmentId).stream().collect(Collectors.toList());
		return l;

	}

	@PostMapping(value = "/api/projects/create")
	public ResponseEntity<String> postProjects(@RequestParam(required = true) Integer departmentId,
			@Valid @RequestBody Project project) {

		try {
			log.info("Proyecto validado correctamente");
			Department depar = departmentService.findDepartmentById(departmentId);
			project.setDepartment(depar);
			log.info("Guardando departamento");
			projectService.saveProject(project);
			log.info("Departamento guardado correctamente");
			return ResponseEntity.ok("Project create");
		} catch (DataAccessException d) {
			log.error("Error:",d);
			return ResponseEntity.badRequest().build();
		}
	}

	@PatchMapping(value = "/api/projects/update")
	public ResponseEntity<String> updateProject(@RequestParam(required = true) Integer departmentId,
			@RequestParam(required = true) Integer projectId, @RequestBody Project project, BindingResult errors) {
		try {
			log.info("Validando proyecto con id:"+projectId);
			projectValidator.validate(project, errors);
			Project dbProject = projectService.findProjectById(projectId);
			if (errors.hasErrors() || dbProject == null) {
				log.error("Se han detectado errores de validacion " + errors.getAllErrors().toString());
				return ResponseEntity.badRequest().body(errors.getAllErrors().toString());
			}
				

			dbProject.setId(projectId);

			if (project.getName() != null)
				dbProject.setName(project.getName());
			if (project.getDescription() != null)
				dbProject.setDescription(project.getDescription());
			log.info("Actualizando proyecto");
			projectService.saveProject(dbProject);
			log.info("Proyecto actualizado");

			return ResponseEntity.ok("Department updated");
		} catch (DataAccessException d) {
			log.error("Error",d);
			return ResponseEntity.badRequest().build();
		}
	}

	// TODO: discutir departmentID (INTERCEPTOR)
	@DeleteMapping(value = "/api/projects/delete")
	public ResponseEntity<String> deleteProjects(@RequestParam(required = true) Integer departmentId,
			@RequestParam(required = true) Integer projectId) {
		try {
			log.info("Borrando proyecto con id: "+projectId);
			projectService.deleteProjectById(projectId);
			return ResponseEntity.ok("Project delete");
		} catch (DataAccessException d) {
			log.error("Error",d);
			return ResponseEntity.notFound().build();
		}
	}

}
