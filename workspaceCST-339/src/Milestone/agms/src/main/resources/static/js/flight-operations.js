/**
 * Initialize the application when DOM is fully loaded
 * Sets up event listeners and initializes dashboard components
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
        console.error('New flight button not found');
    }
});

/**
 * Central initialization function that sets up all dashboard components
 * Initializes forms, buttons, and event listeners for the operations dashboard
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
            console.log('Form submitted');
            handleNewFlightSubmit(e);
        });
    } else {
        console.error('New flight form not found');
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
    
    // Debug log form data
    console.log('Form data entries:');
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value}`);
    }
    
    formData.forEach((value, key) => {
        if (key === 'scheduledDeparture' || key === 'scheduledArrival') {
            flightData[key] = value ? value + ':00' : null;
        } else {
            flightData[key] = value;
        }
    });
    
    flightData.status = 'SCHEDULED';
    
    console.log('Submitting flight data:', JSON.stringify(flightData, null, 2));

    fetch('/operations/flights/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(flightData)
    })
    .then(response => {
        console.log('Response:', response);
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Error response:', text);
                throw new Error('Network response was not ok');
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        if (data.success) {
            const modal = bootstrap.Modal.getInstance(document.querySelector('#newFlightModal'));
            if (modal) {
                modal.hide();
            }
            refreshDashboard();  // Add this line
        } else {
            alert('Failed to create flight: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error creating flight: ' + error.message);
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
            showFlightStatusModal(flightNumber);
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
 * Handles maintenance form submission
 * @param {Event} e - The form submission event
 */
function handleMaintenanceSubmit(e) {
    e.preventDefault();
    console.log('Processing maintenance submission');
    
    const form = e.target;
    const formData = new FormData(form);
    
    // Debug logging
    console.log('Form data before submission:');
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value}`);
    }
    
    // Validate registration number
    const registrationNumber = formData.get('registrationNumber');
    if (!registrationNumber) {
        alert('Missing aircraft registration number');
        return;
    }

    // Convert to URL parameters
    const params = new URLSearchParams(formData);

    // Submit form
    fetch('/operations/aircraft/maintenance', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        console.log('Server response:', data);
        if (data.success) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('maintenanceModal'));
            modal.hide();
            refreshDashboard();
            alert('Maintenance scheduled successfully');
        } else {
            alert('Failed to schedule maintenance: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error scheduling maintenance');
    });
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
    
    // Get aircraft details
    fetch(`/operations/aircraft/${registrationNumber}`)
        .then(response => response.json())
        .then(aircraft => {
            const modal = document.getElementById('maintenanceModal');
            if (modal) {
                // Set hidden registration field
                const regInput = modal.querySelector('#maintenance-registration');
                if (regInput) {
                    regInput.value = registrationNumber;
                }
                
                // Set display field
                const displayInput = modal.querySelector('#maintenance-aircraft-display');
                if (displayInput) {
                    displayInput.value = `${registrationNumber} - ${aircraft.model}`;
                }
                
                // Set minimum date to today
                const dateInput = modal.querySelector('input[name="maintenanceDate"]');
                if (dateInput) {
                    const today = new Date().toISOString().split('T')[0];
                    dateInput.min = today;
                    dateInput.value = today;
                }
                
                // Show modal
                const bsModal = new bootstrap.Modal(modal);
                bsModal.show();
            }
        })
        .catch(error => {
            console.error('Error fetching aircraft details:', error);
            alert('Error loading aircraft details');
        });
}

/**
 * Displays the aircraft status modal
 * @param {string} registration - The aircraft registration number
 */
function showAircraftStatusModal(registration) {
    const modal = document.querySelector('#aircraftStatusModal');
    if (modal) {
        document.querySelector('#registrationNumber').value = registration;
        new bootstrap.Modal(modal).show();
    }
}

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
        fetch(`/operations/flights/${flightNumber}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.reload();
            } else {
                alert('Failed to delete flight: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error deleting flight:', error);
            alert('Error deleting flight');
        });
    }
}

/**
 * Updates flight information in the system
 * @param {Object} flightData - Updated flight information
 */
function updateFlight(flightData) {
    fetch('/operations/flights/update', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(flightData)
    })
    .then(response => response.json())
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
        alert('Error updating flight');
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