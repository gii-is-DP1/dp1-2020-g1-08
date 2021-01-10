package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.Team;
import org.springframework.samples.petclinic.model.UserTW;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class UserTWServiceTests {                
        @Autowired
	protected UserTWService userTWService;
        @Autowired
    protected TeamService teamService;
    @Test
    @Transactional
    void shouldInsertUserIntoDataBaseAndGenerateId() {
    	
    	Team team = teamService.findTeamById(1);
        
    	 UserTW user = new UserTW();
         user.setName("Andres");
         user.setLastname("Puertas");
         user.setEmail("andrespuertas@cyber");
         user.setPassword("123456789");
         user.setRole(Role.employee);
         user.setTeam(team);
         
         
         userTWService.saveUser(user);
         try {
                 this.userTWService.saveUser(user);
             } catch (DataAccessException ex) {
                 Logger.getLogger(UserTWServiceTests.class.getName()).log(Level.SEVERE, null, ex);
             }

         assertThat(user.getId()).isNotNull();	
    }
        
        
    @Test
    void shouldFindUserById() {
    	UserTW user3 = this.userTWService.findUserById(3);
    	assertThat(user3.getName()).isEqualTo("Maria");
    	assertThat(user3.getLastname()).isEqualTo("Torres");
    	
    }
        
    
    @Test
    void shouldFindUserByName() {
    	Collection<UserTW> users = this.userTWService.findUserByName("Julia");
    	assertThat(users.size()).isEqualTo(1);
    	
    	users = this.userTWService.findUserByName("Paco");
    	assertThat(users.isEmpty()).isTrue();
    }
    
    @Test
    void shouldFindUserByLastName() {
    	Collection<UserTW> users = this.userTWService.findUserByLastName("Calle");
    	assertThat(users.size()).isEqualTo(1);
    	
    	users = this.userTWService.findUserByLastName("Paez");
    	assertThat(users.isEmpty()).isTrue();
    }
    
    @Test
    @Transactional
    void shouldDeleteUserById() {
    	
    	
    	this.userTWService.deleteUserById(6);
         UserTW user = this.userTWService.findUserById(6);
         assertThat(user).isNull();
    	
    }



}