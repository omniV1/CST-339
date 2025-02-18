/**
 * Print schedule function
 * Fetches the schedule and triggers a download of the gate-schedule.txt file
 */
function printSchedule() {
    fetch('/gates/assignments/print')
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'gate-schedule.txt';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();
        })
        .catch(error => {
            console.error('Error printing schedule:', error);
            alert('Error printing schedule');
        });
}

/**
 * Show update modal
 * Fetches assignment details and populates the update modal
 * @param {number} assignmentId - The ID of the assignment to update
 */
function showUpdateModal(assignmentId) {
    fetch(`/gates/assignments/${assignmentId}`)
        .then(response => response.json())
        .then(assignment => {
            document.getElementById('editAssignmentId').value = assignment.id;
            document.getElementById('editGateId').value = assignment.gateId;
            document.getElementById('editFlightNumber').value = assignment.flightNumber;
            document.getElementById('editStartTime').value = formatDateTime(assignment.startTime);
            document.getElementById('editEndTime').value = formatDateTime(assignment.endTime);
            
            const modal = new bootstrap.Modal(document.getElementById('updateAssignmentModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching assignment:', error);
            alert('Error loading assignment details');
        });
}

/**
 * Helper function to format datetime for input fields
 * @param {string} dateString - The date string to format
 * @returns {string} Formatted date string for input fields
 */
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
}