package ee.taltech.ite4120.parking.service;

import ee.taltech.ite4120.parking.api.ParkingApplicationQueryParams;
import ee.taltech.ite4120.parking.model.ParkingApplication;
import ee.taltech.ite4120.parking.model.ParkingDecision;
import ee.taltech.ite4120.parking.model.ParkingPermit;
import ee.taltech.ite4120.parking.repository.ParkingRepository;
import java.time.LocalDate;
import java.security.SecureRandom;
import java.util.List;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.helex.commons.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkingService {

    private final ParkingRepository repository;

    public ParkingService(ParkingRepository repository) {
        this.repository = repository;
}


@Transactional
public Long createApplication(
        String personalCode,
        LocalDate applicationDate,
        String reason) {

    return repository.insertApplication(
            personalCode,
            applicationDate,
            reason);
}

@Transactional
public Long createApplication(
        String personalCode,
        LocalDate applicationDate) {

    return createApplication(personalCode, applicationDate, null);
}


    @Transactional
    public Long createApprovedApplication(
            String personalCode,
            LocalDate applicationDate) {

        Long applicationId =
                repository.insertApplication(personalCode, applicationDate);

        repository.insertApprovedDecision(applicationId, applicationDate);
            repository.updateApplicationStatusToDecided(applicationId);

        return applicationId;
    }

        public List<ParkingApplication> searchApplications(
                ParkingApplicationQueryParams params) {
            return repository.searchApplications(params);
        }

        public int countApplications(ParkingApplicationQueryParams params) {
            return repository.countApplications(params);
        }

        public ParkingApplication requireApplication(Long applicationId) {
            ParkingApplication application =
                    repository.loadApplication(applicationId);
            if (application == null) {
                throw new NotFoundException(
                        "parking application " + applicationId + " not found");
            }
            return application;
        }

        @Transactional
    public Long recordDecision(
            Long applicationId,
            LocalDate decisionDate,
            String decisionType,
            String decisionReason) {

        requireApplication(applicationId);

        if (decisionDate == null) {
            throw new ApiClientException("Decision date is required");
        }

        if (!"APPROVED".equals(decisionType)
                && !"REJECTED".equals(decisionType)) {
            throw new ApiClientException(
                    "Decision type must be APPROVED or REJECTED");
        }

        if (repository.loadDecision(applicationId) != null) {
            throw new ConflictException(
                    "Application already has a decision");
        }

        try {
            Long decisionId = repository.insertDecision(
                    applicationId,
                    decisionDate,
                    decisionType,
                    decisionReason);

            repository.updateApplicationStatusToDecided(applicationId);

            return decisionId;
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Application already has a decision");
        }
    }

    public ParkingDecision loadDecision(Long applicationId) {
            return repository.loadDecision(applicationId);
        }

        public ParkingPermit loadPermitForApplication(Long applicationId) {
            return repository.loadPermitForApplication(applicationId);
        }

        public ParkingPermit requirePermit(Long permitId) {
            ParkingPermit permit = repository.loadPermit(permitId);
            if (permit == null) {
                throw new NotFoundException(
                        "parking permit " + permitId + " not found");
            }
            return permit;
        }

    @Transactional
    public ParkingPermit issuePermit(
            Long applicationId,
            String permitNumber,
            LocalDate validFrom,
            LocalDate validUntil) {

        requireApplication(applicationId);

        if (validFrom == null || validUntil == null) {
            throw new ApiClientException(
                    "validFrom and validUntil are required");
        }
        if (validUntil.isBefore(validFrom)) {
            throw new ApiClientException(
                    "validUntil must not be earlier than validFrom");
        }

        // Business rule:
        // A parking permit can only be issued for an APPROVED application.
        if (!repository.hasApprovedDecision(applicationId)) {
            throw new ConflictException(
                    "parkingPermit",
                    "parking permit cannot be issued without an APPROVED decision");
        }

        if (repository.countPermitsForApplication(applicationId) > 0) {
            throw new ConflictException(
                    "parkingPermit",
                    "a parking permit has already been issued for this application");
        }

        if (repository.permitNumberExists(permitNumber)) {
            throw new ConflictException(
                    "parkingPermit",
                    "permit number " + permitNumber + " is already in use");
        }

        Long permitId;
        try {
            permitId = repository.insertPermit(
                    applicationId,
                    permitNumber,
                    validFrom,
                    validUntil);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "parkingPermit",
                    "the permit conflicts with an existing application or permit number");
        }

        return repository.loadPermit(permitId);
    }

    @Transactional
    public ParkingPermit issuePermit(
            Long applicationId,
            LocalDate validFrom,
            LocalDate validUntil) {
        String permitNumber = generatePermitNumber();
        return issuePermit(
                applicationId,
                permitNumber,
                validFrom,
                validUntil);
    }

    private String generatePermitNumber() {
        SecureRandom random = new SecureRandom();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int attempts = 0;
        while (attempts++ < 100) {
            StringBuilder number = new StringBuilder("PARK-");
            for (int i = 0; i < 12; i++) {
                number.append(alphabet.charAt(
                        random.nextInt(alphabet.length())));
            }
            String result = number.toString();
            if (!repository.permitNumberExists(result)) {
                return result;
            }
        }
        throw new ConflictException(
                "parkingPermit",
                "could not generate a unique parking permit number");
    }

}
