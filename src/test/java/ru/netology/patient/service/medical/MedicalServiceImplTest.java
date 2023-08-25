package ru.netology.patient.service.medical;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedicalServiceImplTest {
    private static PatientInfoRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoRepository.class);

    @BeforeAll
    public static void createPatient() {
        Mockito.when(patientInfoFileRepositoryMock.getById(Mockito.anyString()))
                .thenReturn(
                        new PatientInfo(
                                "P1", "Ivan", "Petrov",
                                LocalDate.of(2002, 1, 1),
                                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))
                        )
                );
    }
    @Test
    void checkBloodPressureTest() {
        SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepositoryMock, sendAlertServiceMock);
        medicalService.checkBloodPressure("P1", new BloodPressure(140, 90));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertServiceMock).send(argumentCaptor.capture());

        assertEquals("Warning, patient with id: P1, need help", argumentCaptor.getValue());
    }

    @Test
    void checkTemperatureTest() {
        SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepositoryMock, sendAlertServiceMock);
        medicalService.checkTemperature("P1", new BigDecimal("35"));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertServiceMock).send(argumentCaptor.capture());

        assertEquals("Warning, patient with id: P1, need help", argumentCaptor.getValue());
    }

    @Test
    void checkNormalTest() {
        SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepositoryMock, sendAlertServiceMock);
        medicalService.checkBloodPressure("P1", new BloodPressure(120, 80));
        medicalService.checkTemperature("P1", new BigDecimal("36.6"));

        Mockito.verify(sendAlertServiceMock, Mockito.times(0)).send(Mockito.anyString());
    }
}