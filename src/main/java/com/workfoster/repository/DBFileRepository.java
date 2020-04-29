package com.workfoster.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.workfoster.model.DBFile;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {

	@Query("select p from DBFile p where p.userId=?1")
	List<DBFile> getByUserId(long userId);

}
