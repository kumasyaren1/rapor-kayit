package com.example.raporkayit.service;

import com.example.raporkayit.repository.MukellefRepository;
import org.springframework.stereotype.Service;
import com.example.raporkayit.dto.MukellefResponse;
import com.example.raporkayit.entity.Mukellef;
import com.example.raporkayit.exception.ApplicationException;
import com.example.raporkayit.exception.ErrorCode;
import org.springframework.util.StringUtils;

@Service
public class SicilService {
    private final MukellefRepository mukellefRepository;

    public SicilService(MukellefRepository mukellefRepository) {
        this.mukellefRepository = mukellefRepository;
    }

    public MukellefResponse mukellefSorgula(
            String vergiKimlikNo,
            String tcKimlikNo) {

        boolean vknVar = StringUtils.hasText(vergiKimlikNo);
        boolean tcknVar = StringUtils.hasText(tcKimlikNo);

        if (!vknVar && !tcknVar) {
            throw new ApplicationException(ErrorCode.VKN_TCKN_ZORUNLU);
        }

        Mukellef mukellef;

        if (vknVar && tcknVar) {
            mukellef = mukellefRepository
                    .findByVergiKimlikNoAndTcKimlikNoAndAktifTrue(vergiKimlikNo, tcKimlikNo)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.MUKELLEF_NOT_FOUND));

        } else if (vknVar) {
            mukellef = mukellefRepository
                    .findByVergiKimlikNoAndAktifTrue(vergiKimlikNo)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.MUKELLEF_NOT_FOUND));

        } else {
            mukellef = mukellefRepository
                    .findByTcKimlikNoAndAktifTrue(tcKimlikNo)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.MUKELLEF_NOT_FOUND));
        }

        return new MukellefResponse(
                mukellef.getVergiKimlikNo(),
                mukellef.getTcKimlikNo(),
                mukellef.getAdSoyadUnvan()
        );
    }
}