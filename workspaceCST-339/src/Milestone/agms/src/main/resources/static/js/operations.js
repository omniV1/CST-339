// Operations Dashboard JavaScript
// This script handles all interactive functionality for the Flight Operations dashboard
// including form submissions, data refreshing, and UI updates.

// Wait for the DOM to be fully loaded before setting up event handlers
document.addEventListener('DOMContentLoaded', function() {
    // Initialize all form handlers and event listeners
    initializeNewGateForm();
    initializeGateChangeForm();
    initializeRefreshButton();
    initializeViewScheduleButton();
    
    // Load initial data when page first loads
    refreshGateStatus();

    // Set up click handlers for gate detail buttons
    // Using data attributes for security and better separation of concerns
    document.querySelectorAll('.gate-details-btn').forEach(button => {
        button.addEventListener('click', function() {
            const gateId = this.getAttribute('data-gate-id');
            showGateDetails(gateId);
        });
    });
});

// Handles the submission of the new gate form
// Creates a new gate in the system and updates the display
function initializeNewGateForm() {
    const form = document.getElementById('newGateForm');
    form.addEventListener('submit', function(e) {
        e.preventDefault(); // Prevent default form submission
        
        // Get form values
        const terminal = document.getElementById('terminal').value;
        const gateNumber = document.getElementById('gateNumber').value;
        
        // Send request to create new gate
        fetch('/operations/new-gate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `terminal=${terminal}&gateNumber=${gateNumber}`
        })
        .then(response => response.text())
        .then(result => {
            alert(result);
            if (result.includes('success')) {
                // Close modal and refresh data on success
                bootstrap.Modal.getInstance(document.getElementById('newGateModal')).hide();
                refreshGateStatus();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to create new gate');
        });
    });
}

// Handles the submission of gate change requests
// Sends request to change gate assignments and updates display
function initializeGateChangeForm() {
    const form = document.getElementById('gateChangeForm');
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Collect all form data
        const data = {
            flightNumber: document.getElementById('flightNumber').value,
            currentGate: document.getElementById('currentGate').value,
            requestedGate: document.getElementById('requestedGate').value,
            reason: document.getElementById('changeReason').value
        };
        
        // Send gate change request
        fetch('/operations/gate-change', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(data)
        })
        .then(response => response.text())
        .then(result => {
            alert(result);
            if (result.includes('success')) {
                // Close modal and refresh data on success
                bootstrap.Modal.getInstance(document.getElementById('gateChangeModal')).hide();
                refreshGateStatus();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to submit gate change request');
        });
    });
}

// Sets up the refresh button functionality
function initializeRefreshButton() {
    document.getElementById('refreshBtn').addEventListener('click', refreshGateStatus);
}

// Sets up the view schedule button functionality
function initializeViewScheduleButton() {
    document.getElementById('viewScheduleBtn').addEventListener('click', function() {
        window.location.href = '/operations/schedule';
    });
}

// Fetches fresh data from the server and updates all display components
function refreshGateStatus() {
    fetch('/operations/refresh')
        .then(response => response.json())
        .then(data => {
            updateGateStatusTable(data.currentStatus);
            updateStatisticsCards(data.statistics);
            updateAlerts(data.alerts);
        })
        .catch(error => console.error('Error refreshing data:', error));
}

// Updates the gate status table with fresh data
function updateGateStatusTable(statuses) {
    const tbody = document.getElementById('gateStatusTable');
    tbody.innerHTML = '';
    
    // Create a new row for each gate status
    Object.entries(statuses).forEach(([gateId, status]) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${gateId}</td>
            <td>
                <span class="badge bg-${status.cssClass}">${status.label}</span>
            </td>
            <td>${status.currentFlight || '-'}</td>
            <td>${status.nextFlight || '-'}</td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary gate-details-btn" 
                            data-gate-id="${gateId}">
                        <i class="fas fa-info-circle"></i>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Updates the statistics cards with fresh data
function updateStatisticsCards(stats) {
    Object.entries(stats).forEach(([key, value]) => {
        const element = document.querySelector(`[data-stat="${key}"]`);
        if (element) {
            element.textContent = value;
        }
    });
}

// Updates the alerts section with any new alerts
function updateAlerts(alerts) {
    const alertsContainer = document.getElementById('alertsContainer');
    alertsContainer.innerHTML = '';
    
    // Create a new alert element for each alert
    alerts.forEach(alert => {
        const alertElement = document.createElement('div');
        alertElement.className = `alert alert-${alert.type} alert-dismissible fade show`;
        alertElement.innerHTML = `
            ${alert.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        alertsContainer.appendChild(alertElement);
    });
}

// Shows detailed information for a specific gate
function showGateDetails(gateId) {
    console.log('Showing details for gate:', gateId);
    // This function can be expanded to show a modal with detailed gate information
    // For now, it just logs to the console
}