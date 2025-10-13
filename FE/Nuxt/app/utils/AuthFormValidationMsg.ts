// utils/validationMessages.ts

/**
 * Common validation error messages for authentication forms
 */
export const ValidationMessages = {
    // Email validation messages
    email: {
        required: "Email is required",
        invalid: "Please enter a valid email address",
        alreadyExists: "This email is already registered",
        notFound: "No account found with this email",
        maxLength: "Email must not exceed 100 characters",
    },

    // Password validation messages
    password: {
        required: "Password is required",
        minLength: "Password must be at least 8 characters long",
        maxLength: "Password must not exceed 128 characters",
        tooShort: "Password is too short (minimum 10 characters)",
        weak: "Password is too weak. Use a stronger password",
        noMatch: "Passwords do not match",
        uppercase: "Password must contain at least one uppercase letter",
        lowercase: "Password must contain at least one lowercase letter",
        number: "Password must contain at least one number",
        specialChar:
            "Password must contain at least one special character (!@#$%^&*)",
        common: "This password is too common. Please choose a different one",
        incorrect: "Incorrect password. Please try again",
    },

    // Name validation messages
    name: {
        required: "Name is required",
        minLength: "Name must be at least 2 characters long",
        maxLength: "Name must not exceed 50 characters",
        invalid:
            "Name can only contain letters, spaces, hyphens and apostrophes",
        onlyLetters: "Name must contain only letters",
    },

    // Username validation messages
    username: {
        required: "Username is required",
        minLength: "Username must be at least 3 characters long",
        maxLength: "Username must not exceed 20 characters",
        invalid:
            "Username can only contain letters, numbers, underscores and hyphens",
        alreadyTaken: "This username is already taken",
        notFound: "Username not found",
    },

    // Phone validation messages
    phone: {
        required: "Phone number is required",
        invalid: "Please enter a valid phone number",
        format: "Phone number format is invalid (e.g., +1234567890)",
    },

    // Date of birth validation messages
    dateOfBirth: {
        required: "Date of birth is required",
        invalid: "Please enter a valid date",
        underage: "You must be at least 18 years old to register",
        future: "Date of birth cannot be in the future",
    },

    // Terms and conditions
    terms: {
        required: "You must accept the terms and conditions",
        mustAgree: "Please agree to the terms of service",
    },

    // General validation messages
    general: {
        required: "This field is required",
        invalid: "Invalid input",
        tooShort: "Input is too short",
        tooLong: "Input is too long",
        serverError: "Server error. Please try again later",
        networkError: "Network error. Please check your connection",
        unknown: "An unexpected error occurred",
    },

    // Login specific
    login: {
        failed: "Login failed. Please check your credentials",
        invalidCredentials: "Invalid email or password",
        accountLocked: "Your account has been locked. Please contact support",
        accountDisabled: "Your account has been disabled",
        tooManyAttempts: "Too many login attempts. Please try again later",
        sessionExpired: "Your session has expired. Please log in again",
    },

    // Registration specific
    register: {
        failed: "Registration failed. Please try again",
        emailTaken: "This email is already registered",
        usernameTaken: "This username is already taken",
        weakPassword: "Please choose a stronger password",
    },

    // Password reset
    passwordReset: {
        emailSent: "Password reset email has been sent",
        invalidToken: "Invalid or expired reset token",
        tokenExpired: "Reset link has expired. Please request a new one",
        success: "Password has been reset successfully",
    },

    // Two-factor authentication
    twoFactor: {
        codeRequired: "Verification code is required",
        invalidCode: "Invalid verification code",
        codeExpired: "Verification code has expired",
        tooManyAttempts: "Too many failed attempts. Please try again later",
    },
} as const;

/**
 * Get validation message with dynamic values
 * @example getValidationMessage('password.minLength', { min: 10 })
 */
export function getValidationMessage(
    path: string,
    params?: Record<string, any>
): string {
    const keys = path.split(".");
    let message: any = ValidationMessages;

    for (const key of keys) {
        message = message[key];
        if (!message) return "Invalid input";
    }

    if (typeof message !== "string") return "Invalid input";

    // Replace dynamic parameters
    if (params) {
        return Object.entries(params).reduce(
            (msg, [key, value]) => msg.replace(`{${key}}`, String(value)),
            message
        );
    }

    return message;
}

/**
 * Common regex patterns for validation
 */
export const ValidationPatterns = {
    email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    password: {
        uppercase: /[A-Z]/,
        lowercase: /[a-z]/,
        number: /[0-9]/,
        specialChar: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/,
        minLength: /.{8,}/,
        strongPassword:
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
    },
    username: /^[a-zA-Z0-9_-]{3,20}$/,
    name: /^[a-zA-Z\s'-]{2,50}$/,
    phone: /^\+?[1-9]\d{1,14}$/,
    url: /^https?:\/\/.+/,
} as const;
