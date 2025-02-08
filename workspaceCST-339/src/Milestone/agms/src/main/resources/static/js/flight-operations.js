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
            const modal = bootstrap.Modal.getInstance(document.querySelector('#newFlightModal'));
            modal.hide();
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
    
    // Get date and time components
    const dateValue = formData.get('maintenanceDate');
    const timeValue = formData.get('maintenanceTime');
    
    if (!dateValue || !timeValue) {
        alert('Please select both date and time');
        return;
    }

    try {
        // Format the date string exactly as the server expects it
        // This will create a string like "2025-02-08 20:00:00"
        const formattedDateTime = `${dateValue} ${timeValue}:00`;
        console.log('Formatted date string:', formattedDateTime);
        
        // Create the data to send
        const submitData = new FormData();
        submitData.append('registrationNumber', formData.get('registrationNumber'));
        submitData.append('maintenanceDate', formattedDateTime);  // This is now properly formatted
        submitData.append('maintenanceType', formData.get('maintenanceType'));
        submitData.append('description', formData.get('description'));

        // Log what we're sending
        console.log('Sending maintenance data:');
        submitData.forEach((value, key) => console.log(`${key}: ${value}`));

        // Send the request
        fetch('/operations/aircraft/maintenance', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
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
                const modal = bootstrap.Modal.getInstance(document.getElementById('maintenanceModal'));
                modal.hide();
                refreshDashboard();
                alert('Maintenance scheduled successfully');
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
function showAircraftStatusModal(registration) {
    console.log('Opening status modal for aircraft:', registration);
    
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
                new bootstrap.Modal(modal).show();
                loadMaintenanceHistory(registration);
            })
            .catch(error => {
                console.error('Error loading aircraft details:', error);
                alert('Failed to load aircraft details');
            });
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
    
    // Convert to URL parameters
    const params = new URLSearchParams(formData);
    
    fetch('/operations/aircraft/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('aircraftStatusModal'));
            modal.hide();
            refreshDashboard();
            alert('Aircraft status updated successfully');
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