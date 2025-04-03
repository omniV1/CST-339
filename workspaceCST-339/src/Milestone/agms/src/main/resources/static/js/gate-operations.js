/**
 * Gate Operations JavaScript Module
 * =================================
 * 
 * This module provides client-side functionality for gate operations in the AGMS system.
 * It handles gate assignment management through AJAX interactions with the server.
 * 
 * Core Functionality:
 * 1. Exporting gate schedules to downloadable text files
 * 2. Managing gate assignment UI interactions (view, create, update, delete)
 * 3. Formatting date/time data for display and form inputs
 * 
 * Dependencies:
 * - Fetch API for AJAX communication
 * - Bootstrap for modal components
 * - DOM manipulation for updating UI elements
 */

/**
 * Exports the current gate schedule to a downloadable text file.
 * 
 * This function:
 * 1. Makes an AJAX request to the server to generate a schedule report
 * 2. Receives the response as a binary blob
 * 3. Creates a temporary anchor element to trigger a file download
 * 4. Initiates the download with the filename 'gate-schedule.txt'
 * 5. Cleans up temporary objects after download starts
 * 
 * Error handling includes console logging and user notification.
 * 
 * @returns {void} This function does not return a value but triggers a file download
 */
function printSchedule() {
    // Request the schedule data from the server as a downloadable file
    fetch('/gates/assignments/print')
        .then(response => response.blob())
        .then(blob => {
            // Create a temporary URL for the blob data
            const url = window.URL.createObjectURL(blob);
            
            // Create a temporary anchor element to trigger the download
            const a = document.createElement('a');
            a.href = url;
            a.download = 'gate-schedule.txt';
            
            // Append, click, and remove the anchor to trigger download
            document.body.appendChild(a);
            a.click();
            
            // Clean up resources to prevent memory leaks
            window.URL.revokeObjectURL(url);
            a.remove();
        })
        .catch(error => {
            // Handle any errors that occur during the fetch operation
            console.error('Error printing schedule:', error);
            alert('Error printing schedule');
        });
}

/**
 * Displays the assignment update modal with pre-populated data.
 * 
 * This function:
 * 1. Fetches assignment details from the server using the provided ID
 * 2. Populates the edit form fields with the retrieved data
 * 3. Formats date/time values for proper display in form inputs
 * 4. Shows the Bootstrap modal dialog for editing
 * 
 * @param {number} assignmentId - The unique identifier of the assignment to update
 * @returns {void} This function does not return a value but displays a modal dialog
 */
function showUpdateModal(assignmentId) {
    // Fetch the assignment details from the server
    fetch(`/gates/assignments/${assignmentId}`)
        .then(response => response.json())
        .then(assignment => {
            // Populate the form fields with the assignment data
            document.getElementById('editAssignmentId').value = assignment.id;
            document.getElementById('editGateId').value = assignment.gateId;
            document.getElementById('editFlightNumber').value = assignment.flightNumber;
            document.getElementById('editStartTime').value = formatDateTime(assignment.startTime);
            document.getElementById('editEndTime').value = formatDateTime(assignment.endTime);
            
            // Initialize and display the Bootstrap modal
            const modal = new bootstrap.Modal(document.getElementById('updateAssignmentModal'));
            modal.show();
        })
        .catch(error => {
            // Handle any errors that occur during the fetch operation
            console.error('Error fetching assignment:', error);
            alert('Error loading assignment details');
        });
}

/**
 * Formats a date string for display in datetime-local input fields.
 * 
 * This utility function:
 * 1. Converts a server-side date string to a JavaScript Date object
 * 2. Formats the date to the ISO format required by datetime-local inputs
 * 3. Trims the string to remove seconds and milliseconds (YYYY-MM-DDTHH:MM)
 * 
 * @param {string} dateString - The date string to format (from server)
 * @returns {string} A formatted date string suitable for datetime-local inputs
 */
function formatDateTime(dateString) {
    // Create a Date object from the input string
    const date = new Date(dateString);
    
    // Format the date to ISO format and return the first 16 characters
    // (YYYY-MM-DDTHH:MM) which is the format required by datetime-local inputs
    return date.toISOString().slice(0, 16);
}