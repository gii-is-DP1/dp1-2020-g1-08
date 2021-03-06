package org.springframework.samples.petclinic.repository;

import java.util.Collection;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.Tag;
import org.springframework.samples.petclinic.model.Team;

public interface TagRepository extends Repository<Tag, Integer> {
    void save(Tag tag) throws DataAccessException;

    void deleteById(Integer id) throws DataAccessException;

    public Tag findById(Integer id);

    @Query("SELECT u FROM Tag u WHERE u.title LIKE :title%")
    public Collection<Tag> findByTitle(@Param("title") String title);

    @Query("SELECT u FROM Tag u WHERE u.title LIKE :title")
    public Tag findByName(@Param("title") String title);
}
