package com.sb13.findex.sync.service;

import com.sb13.findex.sync.dto.response.AutoSyncConfigDto;
import com.sb13.findex.indexinfo.entity.IndexInfo;
import com.sb13.findex.sync.entity.AutoSyncConfig;
import com.sb13.findex.sync.repository.AutoSyncConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 자동 연동 설정 관리 서비스
@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    // 지수 등록 여부를 먼저 검증한 뒤 자동 연동 설정 등록
    public AutoSyncConfigDto create(AutoSyncConfigCommand command) {
        AutoSyncConfig saved = autoSyncConfigRepository.save(
                AutoSyncConfig.builder()
                        .indexInfo(command.indexInfo())
                        .enabled(command.enabled())
                        .build());
        return toDto(saved);
    }

    // 활성화 여부만 토글
    public AutoSyncConfigDto update(Long id, boolean enabled) {
        AutoSyncConfig config = autoSyncConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자동 연동 설정입니다."));
        config.setEnabled(enabled);
        return toDto(config);
    }

    // AutoSyncConfig + IndexInfo를 조합해 응답 DTO를 만들기
    private AutoSyncConfigDto toDto(AutoSyncConfig config) {
        IndexInfo indexInfo = config.getIndexInfo();
        return new AutoSyncConfigDto(config.getId(), indexInfo.getId(),
                indexInfo.getIndexClassification(), indexInfo.getIndexName(), config.isEnabled());
    }
}