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

/**
 * Initialize the application when DOM is fully loaded
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    initializeDashboard();
    
    // Add specific handler for new flight button
    const newFlightBtn = document.querySelector('[data-bs-target="#newFlightModal"]');
    if (newFlightBtn) {
        console.log('Found new flight button');
        newFlightBtn.addEventListener('click', function() {
            console.log('New flight button clicked');
            const modal = new bootstrap.Modal(document.getElementById('newFlightModal'));
            modal.show();
        });
    } else {
        console.log('New flight button not found - this is expected if not on a page with flight creation.');
    }

    // Add maintenance date initialization
    const maintenanceDateInput = document.getElementById('maintenanceDate');
    if (maintenanceDateInput) {
        // Set minimum date to now
        const now = new Date();
        const minDateTime = formatDateForInput(now);
        maintenanceDateInput.min = minDateTime;
        
        // Set default value to tomorrow at current time
        const tomorrow = new Date(now);
        tomorrow.setDate(tomorrow.getDate() + 1);
        const tomorrowFormatted = formatDateForInput(tomorrow);
        maintenanceDateInput.value = tomorrowFormatted;
    }

    // Initialize aircraft status form
    const statusForm = document.getElementById('aircraftStatusForm');
    if (statusForm) {
        statusForm.addEventListener('submit', handleAircraftStatusUpdate);
    }

    // Fix for the aircraft status modal
    const aircraftStatusModal = document.getElementById('aircraftStatusModal');
    if (aircraftStatusModal) {
        // Fix for when clicking on the X button
        const closeButtons = aircraftStatusModal.querySelectorAll('.btn-close, .btn[data-bs-dismiss="modal"]');
        closeButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Force cleanup after modal is closed
                forceCleanupModal();
            });
        });
        
        // Also handle the hidden.bs.modal event
        aircraftStatusModal.addEventListener('hidden.bs.modal', function() {
            // Force cleanup after modal is hidden
            forceCleanupModal();
        });
    }
});

function showUpdateStatusModal(flightNumber) {
    // Set the flight number in the modal
    document.getElementById('statusFlightNumber').value = flightNumber;
    
    // Show the modal
    const modal = new bootstrap.Modal(document.getElementById('updateStatusModal'));
    modal.show();
}

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
 * Utility function to get CSRF token and header
 * @returns {Object} Object containing token and header name
 */
function getCsrfTokenInfo() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return { token, header };
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
            
            // Combine date and time inputs for departure
            const departureDate = document.getElementById('departureDate').value;
            const departureTime = document.getElementById('departureTime').value;
            const scheduledDeparture = `${departureDate}T${departureTime}`;
            
            // Combine date and time inputs for arrival
            const arrivalDate = document.getElementById('arrivalDate').value;
            const arrivalTime = document.getElementById('arrivalTime').value;
            const scheduledArrival = `${arrivalDate}T${arrivalTime}`;
            
            // Create flight data object
            const flightData = {
                flightNumber: form.elements['flightNumber'].value,
                airlineCode: form.elements['airlineCode'].value,
                origin: form.elements['origin'].value,
                destination: form.elements['destination'].value,
                assignedAircraft: form.elements['assignedAircraft'].value,
                scheduledDeparture: scheduledDeparture,
                scheduledArrival: scheduledArrival,
                status: 'SCHEDULED'
            };
            
            // Get CSRF token info
            const { token, header } = getCsrfTokenInfo();
            
            // Submit as JSON with proper modal cleanup
            fetch('/operations/flights/create', {
                method: 'POST',
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
                    // Properly close the modal
                    const modalElement = document.getElementById('newFlightModal');
                    const modalInstance = bootstrap.Modal.getInstance(modalElement);
                    modalInstance.hide();
                    
                    // Remove modal backdrop and reset body
                    document.body.classList.remove('modal-open');
                    const backdrops = document.getElementsByClassName('modal-backdrop');
                    while(backdrops.length > 0) {
                        backdrops[0].remove();
                    }
                    
                    // Reset overflow
                    document.body.style.overflow = '';
                    document.body.style.paddingRight = '';
                    
                    // Refresh the page
                    window.location.reload();
                } else {
                    alert('Failed to create flight: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error creating flight: ' + error.message);
            });
        });
    }
}

// Add event listener for modal hidden event - with null check
const newFlightModal = document.getElementById('newFlightModal');
if (newFlightModal) {
    newFlightModal.addEventListener('hidden.bs.modal', function () {
        // Clean up modal artifacts
        document.body.classList.remove('modal-open');
        const backdrops = document.getElementsByClassName('modal-backdrop');
        while(backdrops.length > 0) {
            backdrops[0].remove();
        }
        document.body.style.overflow = '';
        document.body.style.paddingRight = '';
    });
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
        forceCleanupModals();

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
    forceCleanupModal();
    
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
 * Formats a date string for display
 * @param {string} dateString - ISO date string
 * @returns {string} Formatted date string in local format
 */
function formatDateTime(dateString) {
    return new Date(dateString).toLocaleString();
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
 * Formats a date object for datetime-local input
 * @param {Date} date - The date to format
 * @returns {string} Formatted date string for input
 */
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
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
 * Handles aircraft status update form submission
 * @param {Event} e - The form submission event
 */
function handleAircraftStatusUpdate(e) {
    e.preventDefault();
    console.log('Processing aircraft status update');
    
    const form = e.target;
    const formData = new FormData(form);
    
    // Get CSRF token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    
    // Convert to URL parameters
    const params = new URLSearchParams(formData);
    
    fetch('/operations/aircraft/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken // Add CSRF header
        },
        body: params
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            closeModalCompletely('aircraftStatusModal');
            window.location.reload();
        } else {
            alert('Failed to update aircraft status: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error updating aircraft status:', error);
        alert('Error updating aircraft status');
    });
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

/**
 * Helper function to properly close modals
 * @param {string} modalId - The ID of the modal to close
 */
function closeModalCompletely(modalId) {
    const modal = bootstrap.Modal.getInstance(document.getElementById(modalId));
    if (modal) {
        modal.hide();
        document.body.classList.remove('modal-open');
        const backdrop = document.querySelector('.modal-backdrop');
        if (backdrop) {
            backdrop.remove();
        }
    }
}

// Helper function to force modal cleanup
function forceCleanupModal() {
    // Remove modal-open class and inline styles from body
    document.body.classList.remove('modal-open');
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
    
    // Remove any modal backdrop elements
    const backdrops = document.querySelectorAll('.modal-backdrop');
    backdrops.forEach(backdrop => {
        backdrop.remove();
    });
}

/**
 * Comprehensive modal cleanup function
 * This completely removes any traces of modals and backdrops
 */
function forceCleanupAllModals() {
  console.log("Forcing complete modal cleanup");
  
  try {
    // 1. Try to use Bootstrap API first if available
    if (typeof bootstrap !== 'undefined') {
      const modalElements = document.querySelectorAll('.modal');
      modalElements.forEach(modalEl => {
        try {
          const bsModal = bootstrap.Modal.getInstance(modalEl);
          if (bsModal) {
            bsModal.hide();
          }
        } catch (e) {
          console.warn('Bootstrap API modal cleanup failed:', e);
        }
      });
    }
    
    // 2. Remove modal classes from body
    document.body.classList.remove('modal-open');
    document.body.removeAttribute('style');
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
    
    // 3. Remove all modal backdrops
    const backdrops = document.querySelectorAll('.modal-backdrop');
    backdrops.forEach(backdrop => {
      backdrop.classList.remove('show');
      backdrop.classList.remove('fade');
      backdrop.parentNode.removeChild(backdrop);
    });
    
    // 4. Reset all modals
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
      modal.classList.remove('show');
      modal.style.display = 'none';
      modal.setAttribute('aria-hidden', 'true');
      modal.removeAttribute('aria-modal');
      modal.removeAttribute('role');
      modal.removeAttribute('style');
    });
    
    // 5. HTML and document root cleanup
    document.documentElement.classList.remove('modal-open');
    document.documentElement.style.overflow = '';
    document.documentElement.style.paddingRight = '';
    
    // 6. Force reflow/repaint
    window.scrollTo(0, window.scrollY);
  } catch (e) {
    console.error("Error during modal cleanup:", e);
  }
}

/**
 * Specialized function to close a modal with proper cleanup
 * @param {string} modalId - ID of the modal to close
 */
function closeModalSafely(modalId) {
  console.log(`Closing modal ${modalId} safely`);
  
  try {
    // First try to use Bootstrap's API
    const modalElement = document.getElementById(modalId);
    if (!modalElement) return;
    
    // Try Bootstrap approach first
    if (typeof bootstrap !== 'undefined') {
      try {
        const bsModal = bootstrap.Modal.getInstance(modalElement);
        if (bsModal) {
          bsModal.hide();
          // Still run our cleanup with delay
          setTimeout(forceCleanupAllModals, 300);
          return;
        }
      } catch (e) {
        console.warn('Bootstrap modal API not available:', e);
      }
    }
    
    // Manual approach
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
    modalElement.setAttribute('aria-hidden', 'true');
    modalElement.removeAttribute('aria-modal');
    modalElement.removeAttribute('role');
    
    // Always run a complete cleanup after a delay
    setTimeout(forceCleanupAllModals, 300);
  } catch (e) {
    console.error("Error closing modal:", e, modalId);
    // Run cleanup anyway as last resort
    forceCleanupAllModals();
  }
}

// Event Delegation for Update Status Button Click
document.addEventListener('DOMContentLoaded', function() {
    document.body.addEventListener('click', function(e) {
        // Check if the clicked element is the specific button we want
        // Use closest() to handle clicks on icons inside the button if any
        const updateButton = e.target.closest('#updateStatusModal .modal-footer .btn-primary');
        
        if (updateButton) {
            e.preventDefault(); // Prevent default button/submit behavior
            console.log("Update Status button click DETECTED via delegation."); // <-- Log 1

            const form = document.getElementById('updateStatusForm');
            if (!form) {
                console.error('Could not find updateStatusForm!');
                return;
            }
            const formData = new FormData(form);

            // Log the form data
            console.log("Form Data:"); // <-- Log 2
            for (let [key, value] of formData.entries()) { 
                console.log(key, value); // <-- Log 3
            }

            // Get CSRF token
            const { token, header } = getCsrfTokenInfo();

            console.log("Sending fetch request to /operations/flights/status"); // <-- Log 4

            fetch('/operations/flights/status', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    [header]: token // Add CSRF header
                },
                body: new URLSearchParams(formData)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    closeModalCompletely('updateStatusModal'); // Use the safe close function
                    window.location.reload();
                } else {
                    alert('Failed to update status: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error updating flight status: ' + error.message);
            });
        }
    });
});

// Replace all existing closeModal functions with our safe version
window.closeModal = closeModalSafely;
window.forceCleanupModal = forceCleanupAllModals;
window.closeModalCompletely = closeModalSafely;

/**
 * Function to clean up all modals
 * This ensures the forceCleanupModals function is defined
 */
function forceCleanupModals() {
    console.log("Forcing cleanup of all modals");
    forceCleanupAllModals();
}