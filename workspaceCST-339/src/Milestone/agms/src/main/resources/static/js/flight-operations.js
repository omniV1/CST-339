/**
 * =====================================================================================
 * AGMS - Airport Gate Management System - Flight Operations JavaScript Module
 * =====================================================================================
 * 
 * This module provides the client-side functionality for the flight operations
 * dashboard in the AGMS application. It handles real-time data updates, user
 * interactions, and form processing for flight and aircraft management.
 * 
 * CORE FUNCTIONALITY:
 * ------------------
 * 1. Dashboard Initialization & Data Refresh
 *    - Initializes the flight operations dashboard on page load
 *    - Provides real-time data updates through AJAX
 *    - Refreshes statistics and active flight information
 *
 * 2. Flight Management
 *    - Create, update, and delete flight records
 *    - Manage flight status transitions (scheduled, boarding, departed, etc.)
 *    - Display detailed flight information
 *
 * 3. Aircraft Management
 *    - Update aircraft status and location
 *    - Schedule and track maintenance activities
 *    - View maintenance history
 *
 * 4. UI Interaction
 *    - Modal management for forms and details
 *    - Form validation and submission
 *    - Error handling and user feedback
 *
 * 5. Security
 *    - CSRF protection for all AJAX requests
 */

// Utility functions
function getCsrfTokenInfo() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    
    if (!token || !header) {
        console.warn('CSRF tokens not found. Token:', token, 'Header:', header);
        return null;
    }
    
    return { token, header };
}

function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function formatDateTime(dateString) {
    return new Date(dateString).toLocaleString();
}

// Modal Management
function forceCleanupAllModals() {
    console.log("Forcing complete modal cleanup");
    
    try {
        // 1. Remove modal classes and styles from body
        document.body.classList.remove('modal-open');
        document.body.style.overflow = '';
        document.body.style.paddingRight = '';
        
        // 2. Remove all modal backdrops
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());
        
        // 3. Reset all modals
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (typeof bootstrap !== 'undefined') {
                try {
                    const bsModal = bootstrap.Modal.getInstance(modal);
                    if (bsModal) {
                        bsModal.hide();
                    }
                } catch (e) {
                    console.warn('Bootstrap modal cleanup failed:', e);
                }
            }
            modal.classList.remove('show');
            modal.style.display = 'none';
            modal.setAttribute('aria-hidden', 'true');
            modal.removeAttribute('aria-modal');
            modal.removeAttribute('role');
        });
        
        // 4. Force reflow
        window.scrollTo(0, window.scrollY);
    } catch (e) {
        console.error("Error during modal cleanup:", e);
    }
}

// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    
    // Test CSRF tokens
    const csrfInfo = getCsrfTokenInfo();
    if (!csrfInfo) {
        console.error('CSRF tokens not found in the page. Form submissions may fail.');
    }
    
    // Initialize dashboard components
    initializeDashboard();
    initializeNewFlightForm();
    
    // Initialize new flight button
    const newFlightBtn = document.querySelector('[data-bs-target="#newFlightModal"]');
    if (newFlightBtn) {
        console.log('Found new flight button');
        newFlightBtn.addEventListener('click', function() {
            console.log('New flight button clicked');
            const modal = new bootstrap.Modal(document.getElementById('newFlightModal'));
            modal.show();
        });
    }

    // Initialize maintenance date
    const maintenanceDateInput = document.getElementById('maintenanceDate');
    if (maintenanceDateInput) {
        const now = new Date();
        maintenanceDateInput.min = formatDateForInput(now);
        
        const tomorrow = new Date(now);
        tomorrow.setDate(tomorrow.getDate() + 1);
        maintenanceDateInput.value = formatDateForInput(tomorrow);
    }

    // Initialize aircraft status form
    const statusForm = document.getElementById('aircraftStatusForm');
    if (statusForm) {
        statusForm.addEventListener('submit', handleAircraftStatusUpdate);
    }

    // Initialize aircraft status modal
    const aircraftStatusModal = document.getElementById('aircraftStatusModal');
    if (aircraftStatusModal) {
        const closeButtons = aircraftStatusModal.querySelectorAll('.btn-close, .btn[data-bs-dismiss="modal"]');
        closeButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Only cleanup if no pending operations
                if (!document.querySelector('button[type="submit"]:disabled')) {
                    forceCleanupAllModals();
                }
            });
        });
        
        aircraftStatusModal.addEventListener('hidden.bs.modal', function() {
            // Only cleanup if no pending operations
            if (!document.querySelector('button[type="submit"]:disabled')) {
                forceCleanupAllModals();
            }
        });
    }
    
    // Initialize modal cleanup for new flight modal
    const newFlightModal = document.getElementById('newFlightModal');
    if (newFlightModal) {
        newFlightModal.addEventListener('hidden.bs.modal', function() {
            // Only cleanup if no pending operations
            if (!document.querySelector('button[type="submit"]:disabled')) {
                forceCleanupAllModals();
            }
        });
    }
});

// Form Handlers
function handleAircraftStatusUpdate(e) {
    e.preventDefault();
    console.log('Processing aircraft status update');
    
    const csrfInfo = getCsrfTokenInfo();
    if (!csrfInfo) {
        alert('CSRF tokens not found. Cannot proceed with update.');
        return;
    }
    
    // Disable form submission button to prevent double-clicks
    const submitButton = e.target.querySelector('button[type="submit"]');
    if (submitButton) {
        submitButton.disabled = true;
    }
    
    const form = e.target;
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);
    
    fetch('/operations/aircraft/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfInfo.header]: csrfInfo.token
        },
        body: params
    })
    .then(response => {
        if (!response.ok) {
            // Re-enable the submit button on error
            if (submitButton) {
                submitButton.disabled = false;
            }
            
            if (response.status === 403) {
                throw new Error('Access denied. Please check your session and try again.');
            }
            throw new Error(`Server error: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            // Only cleanup and reload on success
            forceCleanupAllModals();
            window.location.reload();
        } else {
            // Re-enable the submit button and show error
            if (submitButton) {
                submitButton.disabled = false;
            }
            alert('Failed to update aircraft status: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error updating aircraft status:', error);
        // Re-enable the submit button and show error
        if (submitButton) {
            submitButton.disabled = false;
        }
        alert('Error updating aircraft status: ' + error.message);
    });
}

// Export functions for global use
window.showUpdateStatusModal = function(flightNumber) {
    document.getElementById('statusFlightNumber').value = flightNumber;
    const modal = new bootstrap.Modal(document.getElementById('updateStatusModal'));
    modal.show();
};

window.showMaintenanceModal = function(registrationNumber) {
    console.log('Opening maintenance modal for aircraft:', registrationNumber);
    
    const modal = document.getElementById('maintenanceModal');
    if (modal) {
        const aircraftSelect = modal.querySelector('#aircraft-display');
        if (aircraftSelect) {
            aircraftSelect.value = registrationNumber;
        }
        
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    }
};

window.showAircraftStatusModal = function(registration) {
    console.log('Opening status modal for aircraft:', registration);
    
    forceCleanupAllModals();
    
    const modal = document.getElementById('aircraftStatusModal');
    if (modal) {
        document.getElementById('status-registration').value = registration;
        
        fetch(`/operations/aircraft/${registration}`)
            .then(response => response.json())
            .then(aircraft => {
                modal.querySelector('select[name="status"]').value = aircraft.status;
                modal.querySelector('input[name="location"]').value = aircraft.currentLocation || '';
                
                const modalInstance = new bootstrap.Modal(modal);
                modalInstance.show();
                loadMaintenanceHistory(registration);
            })
            .catch(error => {
                console.error('Error loading aircraft details:', error);
                alert('Failed to load aircraft details');
            });
    }
};

// Assign modal cleanup functions
window.closeModal = forceCleanupAllModals;
window.forceCleanupModal = forceCleanupAllModals;
window.closeModalCompletely = forceCleanupAllModals;
window.forceCleanupModals = forceCleanupAllModals;

/**
 * Initialize the application when DOM is fully loaded
 * Sets up event listeners and initializes dashboard components
 */
function initializeDashboard() {
    try {
        initializeNewFlightForm();
        initializeEditForms();
        initializeMaintenanceForm();
        initializeStatusUpdates();
        initializeRefreshButton();
        initializeDetailsViewer();
        
        // Add delete button handlers
        document.querySelectorAll('.delete-flight-btn').forEach(button => {
            button.addEventListener('click', function() {
                const flightNumber = this.dataset.flightNumber;
                deleteFlight(flightNumber);
            });
        });
    } catch (error) {
        console.error('Error during dashboard initialization:', error);
    }
}

/**
 * Initializes the new flight form
 * Sets up event listeners for form submission
 */
function initializeNewFlightForm() {
    const form = document.querySelector('#newFlightForm');
    if (form) {
        console.log('Found new flight form');
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const csrfInfo = getCsrfTokenInfo();
            if (!csrfInfo) {
                showFeedback('error', 'CSRF tokens not found. Cannot proceed with flight creation.');
                return;
            }

            // Disable submit button and show loading state
            const submitButton = form.querySelector('button[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating...';
            }
            
            // Get and validate form data
            try {
                // Get date and time values
                const departureDate = document.getElementById('departureDate').value;
                const departureTime = document.getElementById('departureTime').value;
                const arrivalDate = document.getElementById('arrivalDate').value;
                const arrivalTime = document.getElementById('arrivalTime').value;

                if (!departureDate || !departureTime || !arrivalDate || !arrivalTime) {
                    throw new Error('Please fill in all date and time fields');
                }

                // Validate dates
                const departureMoment = new Date(`${departureDate}T${departureTime}`);
                const arrivalMoment = new Date(`${arrivalDate}T${arrivalTime}`);

                if (isNaN(departureMoment.getTime()) || isNaN(arrivalMoment.getTime())) {
                    throw new Error('Invalid date or time format');
                }

                if (arrivalMoment <= departureMoment) {
                    throw new Error('Arrival time must be after departure time');
                }

                // Format dates for server (ISO format)
                const scheduledDeparture = departureMoment.toISOString();
                const scheduledArrival = arrivalMoment.toISOString();
                
                // Validate other required fields
                const flightNumber = form.elements['flightNumber'].value.trim();
                const airlineCode = form.elements['airlineCode'].value.trim();
                const origin = form.elements['origin'].value.trim();
                const destination = form.elements['destination'].value.trim();
                const assignedAircraft = form.elements['assignedAircraft'].value;

                if (!flightNumber || !airlineCode || !origin || !destination || !assignedAircraft) {
                    throw new Error('Please fill in all required fields');
                }

                // Create flight data object
                const flightData = {
                    flightNumber: flightNumber,
                    airlineCode: airlineCode.toUpperCase(),
                    origin: origin.toUpperCase(),
                    destination: destination.toUpperCase(),
                    assignedAircraft: assignedAircraft,
                    scheduledDeparture: scheduledDeparture,
                    scheduledArrival: scheduledArrival,
                    status: 'SCHEDULED'
                };

                // Show creating feedback
                showFeedback('info', 'Creating flight...');
                
                // Submit as JSON
                fetch('/operations/flights/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfInfo.header]: csrfInfo.token
                    },
                    body: JSON.stringify(flightData)
                })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(data => {
                            throw new Error(data.message || `Server error: ${response.status}`);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        showFeedback('success', 'Flight created successfully!');
                        setTimeout(() => {
                            forceCleanupAllModals();
                            window.location.reload();
                        }, 1000);
                    } else {
                        throw new Error(data.message || 'Failed to create flight');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showFeedback('error', `Error creating flight: ${error.message}`);
                    resetSubmitButton(submitButton);
                });

            } catch (error) {
                console.error('Validation error:', error);
                showFeedback('error', error.message);
                resetSubmitButton(submitButton);
            }
        });
    }
}

// Helper function to show feedback to the user
function showFeedback(type, message) {
    // Remove any existing feedback
    const existingFeedback = document.querySelector('.feedback-alert');
    if (existingFeedback) {
        existingFeedback.remove();
    }

    // Create new feedback element
    const feedback = document.createElement('div');
    feedback.className = `alert alert-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info'} feedback-alert`;
    feedback.style.position = 'fixed';
    feedback.style.top = '20px';
    feedback.style.right = '20px';
    feedback.style.zIndex = '9999';
    feedback.innerHTML = message;

    // Add to document
    document.body.appendChild(feedback);

    // Remove after 5 seconds if it's a success message
    if (type === 'success') {
        setTimeout(() => {
            feedback.remove();
        }, 5000);
    }
}

// Helper function to reset submit button
function resetSubmitButton(button) {
    if (button) {
        button.disabled = false;
        button.innerHTML = 'Create Flight';
    }
}

/**
 * Handles the submission of a new flight form
 * @param {Event} e - The form submission event
 */
function handleNewFlightSubmit(e) {
    e.preventDefault();
    console.log('Processing new flight submission');
    
    const formData = new FormData(e.target);
    const flightData = {};
    
    formData.forEach((value, key) => {
        if (key === 'scheduledDeparture' || key === 'scheduledArrival') {
            flightData[key] = value ? value + ':00' : null;
        } else {
            flightData[key] = value;
        }
    });
    
    flightData.status = 'SCHEDULED';
    
    fetch('/operations/flights/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(flightData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Close modal and remove backdrop
            const modal = bootstrap.Modal.getInstance(document.getElementById('newFlightModal'));
            modal.hide();
            document.body.classList.remove('modal-open');
            const backdrop = document.querySelector('.modal-backdrop');
            if (backdrop) {
                backdrop.remove();
            }
            // Refresh the page
            window.location.reload();
        } else {
            alert('Failed to create flight: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error creating flight');
    });
}

/**
 * Initializes status update handlers for flights and aircraft
 * Sets up event listeners for status buttons and detail viewers
 */
function initializeStatusUpdates() {
    // Update status buttons for flights
    document.querySelectorAll('.update-status-btn').forEach(button => {
        button.addEventListener('click', function() {
            const flightNumber = this.dataset.flightNumber;
            showUpdateStatusModal(flightNumber);
        });
    });

    // View details buttons for flights
    document.querySelectorAll('.view-details-btn').forEach(button => {
        button.addEventListener('click', function() {
            const flightNumber = this.dataset.flightNumber;
            showFlightDetails(flightNumber);
        });
    });

    // Aircraft status buttons
    document.querySelectorAll('.aircraft-status-btn').forEach(button => {
        button.addEventListener('click', function() {
            const registration = this.dataset.registration;
            showAircraftStatusModal(registration);
        });
    });

    // Maintenance buttons
    document.querySelectorAll('.maintenance-btn').forEach(button => {
        button.addEventListener('click', function() {
            const registration = this.dataset.registration;
            showMaintenanceModal(registration);
        });
    });
}

/**
 * Initializes the maintenance form
 * Sets up event listeners for maintenance scheduling
 */
function initializeMaintenanceForm() {
    const form = document.getElementById('maintenanceForm');
    if (form) {
        form.addEventListener('submit', handleMaintenanceSubmit);
    }
}

/**
 * Enhanced handle maintenance submit
 */
function handleMaintenanceSubmit(e) {
    e.preventDefault();
    console.log('Processing maintenance submission');
    
    const form = e.target;
    const formData = new FormData(form);
    
    // Get date and time components
    const dateValue = formData.get('maintenanceDate');
    const timeValue = formData.get('maintenanceTime');
    
    if (!dateValue || !timeValue) {
        alert('Please select both date and time');
        return;
    }

    try {
        // Format the date string exactly as the server expects it
        const formattedDateTime = `${dateValue} ${timeValue}:00`;
        console.log('Formatted date string:', formattedDateTime);
        
        // Create the data to send
        const submitData = new FormData();
        submitData.append('registrationNumber', formData.get('registrationNumber'));
        submitData.append('maintenanceDate', formattedDateTime);
        submitData.append('maintenanceType', formData.get('maintenanceType'));
        submitData.append('description', formData.get('description'));

        // Get CSRF token
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        // Close modal FIRST before making the API request
        forceCleanupAllModals();

        // Log what we're sending
        console.log('Sending maintenance data:');
        submitData.forEach((value, key) => console.log(`${key}: ${value}`));

        // Send the request
        fetch('/operations/aircraft/maintenance', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken // Add CSRF header
            },
            body: new URLSearchParams(submitData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Server response:', data);
            if (data.success) {
                // Use window.location.reload() instead
                window.location.reload();
            } else {
                alert('Failed to schedule maintenance: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error scheduling maintenance:', error);
            alert('Failed to schedule maintenance: ' + error.message);
        });
    } catch (error) {
        console.error('Error formatting date:', error);
        alert('Invalid date or time format. Please try again.');
    }
}

/**
 * Displays the flight status modal for a specific flight
 * @param {string} flightNumber - The flight identifier
 */
function showFlightStatusModal(flightNumber) {
    const modal = document.querySelector('#flightStatusModal');
    if (modal) {
        document.querySelector('#flightNumber').value = flightNumber;
        new bootstrap.Modal(modal).show();
    }
}

/**
 * Displays the maintenance modal for a specific aircraft
 * @param {string} registrationNumber - The aircraft registration number
 */
function showMaintenanceModal(registrationNumber) {
    console.log('Opening maintenance modal for aircraft:', registrationNumber);
    
    const modal = document.getElementById('maintenanceModal');
    if (modal) {
        // Set the selected aircraft in the dropdown
        const aircraftSelect = modal.querySelector('#aircraft-display');
        if (aircraftSelect) {
            aircraftSelect.value = registrationNumber;
        }
        
        // Show the modal
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    }
}

/**
 * Displays the aircraft status modal
 * @param {string} registration - The aircraft registration number
 */
window.showAircraftStatusModal = function(registration) {
    console.log('Opening status modal for aircraft:', registration);
    
    // First ensure any existing modals are properly cleaned up
    forceCleanupAllModals();
    
    const modal = document.getElementById('aircraftStatusModal');
    if (modal) {
        // Set the registration number
        document.getElementById('status-registration').value = registration;
        
        // Fetch and display current aircraft details
        fetch(`/operations/aircraft/${registration}`)
            .then(response => response.json())
            .then(aircraft => {
                modal.querySelector('select[name="status"]').value = aircraft.status;
                modal.querySelector('input[name="location"]').value = aircraft.currentLocation || '';
                
                // Show modal and load history
                const modalInstance = new bootstrap.Modal(modal);
                modalInstance.show();
                loadMaintenanceHistory(registration);
            })
            .catch(error => {
                console.error('Error loading aircraft details:', error);
                alert('Failed to load aircraft details');
            });
    }
};

/**
 * Initializes the flight details viewer
 * Sets up event listeners for viewing flight details
 */
function initializeDetailsViewer() {
    document.querySelectorAll('.view-details-btn').forEach(button => {
        button.addEventListener('click', function() {
            const flightNumber = this.dataset.flightNumber;
            showFlightDetails(flightNumber);
        });
    });
}

/**
 * Displays detailed information for a specific flight
 * @param {string} flightNumber - The flight identifier
 */
function showFlightDetails(flightNumber) {
    fetch(`/operations/flights/${flightNumber}`)
        .then(response => response.json())
        .then(data => {
            const detailsContent = document.querySelector('#flightDetailsContent');
            if (detailsContent) {
                detailsContent.innerHTML = formatFlightDetails(data);
                new bootstrap.Modal(document.querySelector('#flightDetailsModal')).show();
            }
        })
        .catch(error => {
            console.error('Error loading flight details:', error);
            alert('Failed to load flight details');
        });
}

/**
 * Formats flight details for display in the modal
 * @param {Object} flightData - The flight information object
 * @returns {string} HTML string containing formatted flight details
 */
function formatFlightDetails(flightData) {
    return `
        <div class="row">
            <div class="col-md-6">
                <h6>Flight Information</h6>
                <p><strong>Flight Number:</strong> ${flightData.flightNumber}</p>
                <p><strong>Route:</strong> ${flightData.origin} → ${flightData.destination}</p>
                <p><strong>Status:</strong> <span class="badge bg-${flightData.status.cssClass}">${flightData.status.label}</span></p>
            </div>
            <div class="col-md-6">
                <h6>Schedule</h6>
                <p><strong>Departure:</strong> ${formatDateTime(flightData.scheduledDeparture)}</p>
                <p><strong>Arrival:</strong> ${formatDateTime(flightData.scheduledArrival)}</p>
                <p><strong>Aircraft:</strong> ${flightData.aircraft}</p>
            </div>
        </div>
    `;
}

/**
 * Initializes the dashboard refresh button
 */
function initializeRefreshButton() {
    const refreshButton = document.querySelector('#refreshButton');
    if (refreshButton) {
        refreshButton.addEventListener('click', () => window.location.reload());
    }
}

/**
 * Closes a modal by its ID
 * @param {string} modalId - The ID of the modal to close
 */
function closeModal(modalId) {
    const modalElement = document.querySelector(`#${modalId}`);
    if (modalElement) {
        const modal = bootstrap.Modal.getInstance(modalElement);
        if (modal) modal.hide();
    }
}

/**
 * Deletes a flight from the system
 * @param {string} flightNumber - The flight to delete
 */
function deleteFlight(flightNumber) {
    if (confirm('Are you sure you want to delete this flight?')) {
        // Get CSRF token info
        const { token, header } = getCsrfTokenInfo();
        
        fetch(`/operations/flights/${flightNumber}`, {
            method: 'DELETE',
            headers: {
                [header]: token // Add CSRF header
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                window.location.reload();
            } else {
                alert('Failed to delete flight: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error deleting flight:', error);
            alert('Error deleting flight: ' + error.message);
        });
    }
}

/**
 * Updates flight information in the system
 * @param {Object} flightData - Updated flight information
 */
function updateFlight(flightData) {
    // Get CSRF token info
    const { token, header } = getCsrfTokenInfo();
    
    fetch('/operations/flights/update', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [header]: token // Add CSRF header
        },
        body: JSON.stringify(flightData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            closeModal('editFlightModal');
            window.location.reload();
        } else {
            alert('Failed to update flight: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error updating flight:', error);
        alert('Error updating flight: ' + error.message);
    });
}

/**
 * Handles the submission of flight edit form
 * @param {Event} e - The form submission event
 */
function handleEditFlightSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const flightData = {};
    
    formData.forEach((value, key) => {
        if (key === 'scheduledDeparture' || key === 'scheduledArrival') {
            flightData[key] = value ? value + ':00' : null;
        } else {
            flightData[key] = value;
        }
    });
    
    updateFlight(flightData);
}

/**
 * Initializes edit form handlers
 * Sets up event listeners for flight editing
 */
function initializeEditForms() {
    const editForm = document.querySelector('#editFlightForm');
    if (editForm) {
        editForm.addEventListener('submit', handleEditFlightSubmit);
    }
}

/**
 * Refreshes the dashboard data via AJAX
 * Updates statistics, flights table, and aircraft table
 */
function refreshDashboard() {
    fetch('/operations/dashboard/data')
        .then(response => response.json())
        .then(data => {
            updateStatisticsCards(data.statistics);
            updateActiveFlightsTable(data.activeFlights);
            updateAircraftTable(data.aircraft);
        })
        .catch(error => console.error('Error refreshing dashboard:', error));
}

/**
 * Updates the statistics cards with new data
 * @param {Object} statistics - Object containing updated statistics
 */
function updateStatisticsCards(statistics) {
    document.querySelector('[data-stat="activeFlights"]').textContent = statistics.activeFlights;
    document.querySelector('[data-stat="availableAircraft"]').textContent = statistics.availableAircraft;
    document.querySelector('[data-stat="maintenanceCount"]').textContent = statistics.maintenanceCount;
    document.querySelector('[data-stat="delayedFlights"]').textContent = statistics.delayedFlights;
}

/**
 * Updates the registration field with a given value
 * @param {string} value - The value to set in the registration field
 */
function updateRegistrationField(value) {
    const registrationField = document.getElementById('maintenance-registration');
    if (registrationField) {
        registrationField.value = value;
    }
}

/**
 * Formats a date for server submission
 * @param {Date} date - The date to format
 * @returns {string} Formatted date string for server
 */
function formatDateForServer(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = '00';
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 * Loads and displays maintenance history for an aircraft
 * @param {string} registrationNumber - The aircraft registration number
 */
function loadMaintenanceHistory(registrationNumber) {
    console.log('Loading maintenance history for:', registrationNumber);
    
    fetch(`/operations/aircraft/${registrationNumber}/maintenance`)
        .then(response => response.json())
        .then(records => {
            const tableBody = document.getElementById('maintenanceHistoryTable');
            if (tableBody) {
                tableBody.innerHTML = records.map(record => `
                    <tr>
                        <td>${formatDateTime(record.scheduledDate)}</td>
                        <td>${record.type}</td>
                        <td><span class="badge bg-${record.status.cssClass}">${record.status.label}</span></td>
                        <td>${record.description}</td>
                    </tr>
                `).join('');
            }
        })
        .catch(error => {
            console.error('Error loading maintenance history:', error);
            alert('Failed to load maintenance history');
        });
}