package com.sb13.findex.sync.repository;

import com.sb13.findex.sync.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {
    // 배치 스케줄러가 자동 연동 대상(활성화된 설정)만 조회할 때 사용
    List<AutoSyncConfig> findByEnabled(boolean enabled);
}