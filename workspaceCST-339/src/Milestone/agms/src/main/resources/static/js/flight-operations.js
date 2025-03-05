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

// Handle status update form submission
document.getElementById('updateStatusForm')?.addEventListener('submit', function(e) {
    e.preventDefault();
    const formData = new FormData(this);

    fetch('/operations/flights/status', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            closeModalCompletely('updateStatusModal');
            window.location.reload();
        } else {
            alert('Failed to update status: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error updating flight status');
    });
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
            
            // Submit as JSON with proper modal cleanup
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
                alert('Error creating flight');
            });
        });
    }
}

// Add event listener for modal hidden event
document.getElementById('newFlightModal').addEventListener('hidden.bs.modal', function () {
    // Clean up modal artifacts
    document.body.classList.remove('modal-open');
    const backdrops = document.getElementsByClassName('modal-backdrop');
    while(backdrops.length > 0) {
        backdrops[0].remove();
    }
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
});

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

// Add event listeners when the DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
  console.log('Setting up modal event handlers');
  
  // Event handler for modal close buttons
  document.addEventListener('click', function(event) {
    const target = event.target;
    
    // Check if the clicked element is a close button of any kind
    if (target.classList.contains('btn-close') || 
        target.hasAttribute('data-bs-dismiss') ||
        target.closest('[data-bs-dismiss="modal"]')) {
      
      console.log('Modal close button clicked');
      setTimeout(forceCleanupAllModals, 300);
    }
  });
  
  // Handle modal backdrop clicks
  document.addEventListener('mousedown', function(event) {
    if (event.target.classList.contains('modal')) {
      console.log('Modal backdrop clicked');
      setTimeout(forceCleanupAllModals, 300);
    }
  });
  
  // Handle Escape key press
  document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
      console.log('ESC key pressed, cleaning up modals');
      setTimeout(forceCleanupAllModals, 300);
    }
  });
  
  // Setup all maintenance form handlers with safety checks
  const maintenanceForm = document.getElementById('maintenanceForm');
  if (maintenanceForm) {
    maintenanceForm.addEventListener('submit', function(e) {
      e.preventDefault();
      
      try {
        // Get form data safely
        const formData = new FormData(maintenanceForm);
        
        // Close modal first
        forceCleanupAllModals();
        
        // Then submit the data
        fetch('/operations/aircraft/maintenance', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: new URLSearchParams(formData)
        })
        .then(response => response.json())
        .then(data => {
          if (data.success) {
            // Reload without animation
            window.location.href = window.location.pathname;
          } else {
            alert('Failed to schedule maintenance: ' + data.message);
          }
        })
        .catch(error => {
          console.error('Error scheduling maintenance:', error);
          alert('Error scheduling maintenance');
        });
      } catch (e) {
        console.error('Error processing form:', e);
        alert('An error occurred while submitting the form');
      }
    });
  }
  
  // Add special handling for all ajax forms
  document.querySelectorAll('form[data-ajax="true"]').forEach(form => {
    form.addEventListener('submit', function(e) {
      e.preventDefault();
      
      // Close any modals first
      forceCleanupAllModals();
      
      // Then process form normally
      const formData = new FormData(form);
      const url = form.getAttribute('action') || window.location.pathname;
      const method = form.getAttribute('method') || 'POST';
      
      fetch(url, {
        method: method,
        body: new URLSearchParams(formData)
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          window.location.href = window.location.pathname;
        } else {
          alert(data.message || 'Operation failed');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('An error occurred');
      });
    });
  });
  
  // Check for stuck modals on load
  setTimeout(function() {
    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop && !document.querySelector('.modal.show')) {
      console.log('Found stuck backdrop on page load, cleaning up');
      forceCleanupAllModals();
    }
  }, 500);
});

// Replace all existing closeModal functions with our safe version
window.closeModal = closeModalSafely;
window.forceCleanupModal = forceCleanupAllModals;
window.closeModalCompletely = closeModalSafely;