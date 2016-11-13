package io.linuxserver.davos.persistence.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import io.linuxserver.davos.persistence.model.ScheduleModel;

public interface ScheduleRepository extends CrudRepository<ScheduleModel, Long> {

    List<ScheduleModel> findAll();
    
    List<ScheduleModel> findByHost_Id(Long hostId);
}
