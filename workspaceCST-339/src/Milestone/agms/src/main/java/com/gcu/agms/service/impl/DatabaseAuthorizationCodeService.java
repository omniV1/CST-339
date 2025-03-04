package com.gcu.agms.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gcu.agms.model.auth.AuthorizationCodeModel;
import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.repository.AuthorizationCodeRepository;
import com.gcu.agms.service.auth.AuthorizationCodeService;

/**
 * Database implementation of the AuthorizationCodeService interface.
 * Provides methods for authorization code management with persistence.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
// Removed @Service annotation since we define this as a bean in config
public class DatabaseAuthorizationCodeService implements AuthorizationCodeService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthorizationCodeService.class);
    private static final String ALPHA_NUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excluding similar-looking chars
    private static final int CODE_LENGTH = 8;
    
    private final AuthorizationCodeRepository authCodeRepository;
    private final SecureRandom secureRandom;
    
    /**
     * Constructor with repository dependency injection.
     * 
     * @param authCodeRepository Repository for authorization code data access
     */
    public DatabaseAuthorizationCodeService(AuthorizationCodeRepository authCodeRepository) {
        this.authCodeRepository = authCodeRepository;
        this.secureRandom = new SecureRandom();
        
        // Add a seed for more randomness
        byte[] seed = secureRandom.generateSeed(8);
        secureRandom.setSeed(seed);
        
        logger.info("Initialized Database Authorization Code Service");
    }
    
    @Override
    public boolean isValidAuthCode(String authCode, UserRole requestedRole) {
        logger.debug("Validating auth code for role: {}", requestedRole);
        
        if (authCode == null || authCode.trim().isEmpty()) {
            logger.warn("Empty or null authorization code provided");
            return false;
        }
        
        Optional<AuthorizationCodeModel> codeRecord = authCodeRepository.findByCode(authCode);
        
        if (codeRecord.isPresent() && codeRecord.get().isValid() && codeRecord.get().getRole() == requestedRole) {
            logger.info("Valid authorization code provided for role: {}", requestedRole);
            return true;
        } else {
            logger.warn("Invalid authorization code provided for role: {}", requestedRole);
            return false;
        }
    }
    
    @Override
    public void markCodeAsUsed(String authCode, UserModel user) {
        logger.debug("Marking authorization code as used: {}", authCode);
        
        Optional<AuthorizationCodeModel> codeRecord = authCodeRepository.findByCode(authCode);
        
        if (codeRecord.isPresent()) {
            AuthorizationCodeModel code = codeRecord.get();
            code.setUsedBy(user.getId());
            code.setUsedAt(LocalDateTime.now());
            
            authCodeRepository.save(code);
            logger.info("Auth code {} marked as used by user {}", authCode, user.getUsername());
        } else {
            logger.warn("Could not mark code as used - code not found: {}", authCode);
        }
    }
    
    @Override
    public String generateNewCode(UserRole role, String description, LocalDateTime expiresAt) {
        logger.debug("Generating new authorization code for role: {}", role);
        
        // Generate a random, secure code
        String newCode = generateSecureRandomCode();
        
        // Create new code record
        AuthorizationCodeModel codeModel = new AuthorizationCodeModel();
        codeModel.setCode(newCode);
        codeModel.setRole(role);
        codeModel.setIsActive(true);
        codeModel.setDescription(description);
        codeModel.setCreatedAt(LocalDateTime.now());
        codeModel.setExpiresAt(expiresAt);
        
        authCodeRepository.save(codeModel);
        logger.info("New authorization code generated for role {}", role);
        
        return newCode;
    }
    
    @Override
    public List<AuthorizationCodeModel> getAllAuthCodes() {
        logger.debug("Retrieving all authorization codes");
        return authCodeRepository.findAll();
    }
    
    @Override
    public boolean deactivateAuthCode(Long id) {
        logger.debug("Deactivating authorization code with ID: {}", id);
        
        Optional<AuthorizationCodeModel> codeRecord = authCodeRepository.findById(id);
        
        if (codeRecord.isPresent()) {
            AuthorizationCodeModel code = codeRecord.get();
            code.setIsActive(false);
            
            authCodeRepository.save(code);
            logger.info("Authorization code with ID {} deactivated", id);
            return true;
        }
        
        logger.warn("Authorization code with ID {} not found for deactivation", id);
        return false;
    }
    
    @Override
    public void deleteAuthCode(Long id) {
        logger.debug("Deleting authorization code with ID: {}", id);
        authCodeRepository.deleteById(id);
        logger.info("Authorization code with ID {} deleted", id);
    }
    
    /**
     * Generates a secure random code of specified length.
     * 
     * @return Randomly generated secure code
     */
    private String generateSecureRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(ALPHA_NUMERIC.length());
            code.append(ALPHA_NUMERIC.charAt(randomIndex));
        }
        
        return code.toString();
    }
}