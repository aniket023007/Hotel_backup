/**
 * GRAND HORIZON HOTEL & SUITES - RECEPTIONIST APPLICATION JAVASCRIPT
 * Real-time frontend connected to Java 21 REST API & MySQL
 */

const API_BASE = '/api';

// Global Application State
const state = {
  stats: {},
  rooms: [],
  filteredRooms: [],
  guests: [],
  filteredGuests: [],
  activeBookings: [],
  allBookings: [],
  showingHistory: false,
  currentRoomFilter: 'all',
  currentRoomTypeFilter: 'all',
  currentRoomSearch: '',
  activeInvoiceBookingId: null,
};

// =============================================================================
// INITIALIZATION
// =============================================================================
document.addEventListener('DOMContentLoaded', () => {
  initClock();
  setupModalBackdrops();
  checkDatabaseHealth();
  fetchAllData();

  // Auto-refresh stats and room availability every 15 seconds
  setInterval(() => {
    fetchStats();
    fetchRooms(false);
  }, 15000);
});

// Live Digital Clock & Date
function initClock() {
  const timeEl = document.getElementById('clockTime');
  const dateEl = document.getElementById('clockDate');

  function update() {
    const now = new Date();
    timeEl.textContent = now.toLocaleTimeString('en-US', {
      hour12: true,
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
    dateEl.textContent = now.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  }

  update();
  setInterval(update, 1000);
}

// Close dialog when clicking backdrop
function setupModalBackdrops() {
  document.querySelectorAll('dialog.custom-modal').forEach((dialog) => {
    dialog.addEventListener('click', (e) => {
      const rect = dialog.getBoundingClientRect();
      const isInDialog =
        rect.top <= e.clientY &&
        e.clientY <= rect.top + rect.height &&
        rect.left <= e.clientX &&
        e.clientX <= rect.left + rect.width;

      if (!isInDialog) {
        dialog.close();
      }
    });
  });
}

// Tab Switching
function switchTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach((pane) => {
    pane.classList.remove('active');
  });
  document.querySelectorAll('.nav-tab-btn').forEach((btn) => {
    btn.classList.remove('active');
    if (btn.getAttribute('data-tab') === tabId) {
      btn.classList.add('active');
    }
  });

  const targetPane = document.getElementById(tabId);
  if (targetPane) {
    targetPane.classList.add('active');
  }

  if (tabId === 'roomsTab') renderRooms();
  if (tabId === 'bookingsTab') renderBookingsTable();
  if (tabId === 'guestsTab') renderGuestsTable();
  if (tabId === 'billingTab') populateBillingLookupDropdown();
}

// =============================================================================
// DATA FETCHING & API INTERACTION
// =============================================================================

async function checkDatabaseHealth() {
  try {
    const res = await fetch(`${API_BASE}/health`);
    const data = await res.json();
    const pill = document.getElementById('dbStatusPill');
    const text = document.getElementById('dbStatusText');

    if (data.status === 'healthy') {
      text.textContent = `MySQL Connected (${data.catalog})`;
      pill.style.background = 'rgba(16, 185, 129, 0.15)';
      pill.style.color = '#34d399';
    } else {
      text.textContent = 'DB Degraded';
      pill.style.background = 'rgba(244, 63, 94, 0.15)';
      pill.style.color = '#fb7185';
    }
  } catch (err) {
    const text = document.getElementById('dbStatusText');
    if (text) text.textContent = 'Server Offline';
  }
}

async function fetchAllData() {
  try {
    await Promise.all([
      fetchStats(),
      fetchRooms(),
      fetchGuests(),
      fetchBookings(),
    ]);
  } catch (err) {
    showToast('Failed to load initial hotel data: ' + err.message, 'error');
  }
}

async function fetchStats() {
  try {
    const res = await fetch(`${API_BASE}/stats`);
    if (!res.ok) throw new Error('Stats fetch error');
    state.stats = await res.json();
    renderStats();
  } catch (err) {
    console.error('Error fetching stats:', err);
  }
}

async function fetchRooms(reRender = true) {
  try {
    const res = await fetch(`${API_BASE}/rooms`);
    if (!res.ok) throw new Error('Rooms fetch error');
    state.rooms = await res.json();
    state.filteredRooms = [...state.rooms];
    document.getElementById('tabRoomsBadge').textContent = state.rooms.length;
    if (reRender) {
      renderRooms();
      populateRoomDropdown();
    }
  } catch (err) {
    console.error('Error fetching rooms:', err);
  }
}

async function fetchGuests() {
  try {
    const res = await fetch(`${API_BASE}/guests`);
    if (!res.ok) throw new Error('Guests fetch error');
    state.guests = await res.json();
    state.filteredGuests = [...state.guests];
    document.getElementById('tabGuestsBadge').textContent = state.guests.length;
    renderGuestsTable();
    populateGuestDropdown();
  } catch (err) {
    console.error('Error fetching guests:', err);
  }
}

async function fetchBookings() {
  try {
    const [activeRes, allRes] = await Promise.all([
      fetch(`${API_BASE}/bookings`),
      fetch(`${API_BASE}/bookings/history`),
    ]);

    if (activeRes.ok) state.activeBookings = await activeRes.json();
    if (allRes.ok) state.allBookings = await allRes.json();

    document.getElementById('tabBookingsBadge').textContent = state.activeBookings.length;
    renderDashboardActiveTable();
    renderBookingsTable();
    populateBillingLookupDropdown();
  } catch (err) {
    console.error('Error fetching bookings:', err);
  }
}

// =============================================================================
// RENDERING FUNCTIONS
// =============================================================================

function formatCurrency(amount) {
  return '₹' + Number(amount || 0).toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

// Render Dashboard KPI Cards & Occupancy Bar
function renderStats() {
  const s = state.stats;
  document.getElementById('statTotalRooms').textContent = s.totalRooms || 0;
  document.getElementById('statAvailableRooms').textContent = s.availableRooms || 0;
  document.getElementById('statBookedRooms').textContent = s.bookedRooms || 0;
  document.getElementById('statTotalGuests').textContent = s.totalGuests || 0;
  document.getElementById('statTotalRevenue').textContent = formatCurrency(s.totalRevenue || 0);

  const occRate = s.occupancyRate || 0;
  document.getElementById('statOccupancyPct').textContent = `${occRate}% occupancy rate`;
  document.getElementById('statAvailablePct').textContent = `${s.availableRooms || 0} suites ready`;
  document.getElementById('occupancyBarFill').style.width = `${occRate}%`;
  document.getElementById('occupancyRatioText').textContent = `${s.bookedRooms || 0} / ${s.totalRooms || 0} Rooms Occupied`;
  document.getElementById('occupancyPercentText').textContent = `${occRate}%`;
}

// Render Dashboard In-House Stays Preview Table
function renderDashboardActiveTable() {
  const tbody = document.getElementById('dashboardActiveTableBody');
  if (!tbody) return;

  if (state.activeBookings.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="7" style="text-align: center; padding: 2rem; color: var(--text-muted);">
          No active guest stays right now. All suites are ready for booking!
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = state.activeBookings.map((b) => `
    <tr>
      <td><strong style="color: var(--text-gold);">#${b.bookingId}</strong></td>
      <td>
        <span class="guest-avatar">${b.guestName.charAt(0).toUpperCase()}</span>
        <strong>${escapeHtml(b.guestName)}</strong>
        <div style="font-size: 0.75rem; color: var(--text-muted);">${escapeHtml(b.guestPhone || '')}</div>
      </td>
      <td><strong>${escapeHtml(b.roomNumber)}</strong></td>
      <td><span class="amenity-chip">${escapeHtml(b.roomType)}</span></td>
      <td>${b.numberOfNights} night(s)</td>
      <td><strong style="color: var(--text-gold);">${formatCurrency(b.totalAmount)}</strong></td>
      <td>
        <div class="table-actions">
          <button class="btn btn-secondary btn-sm" onclick="viewBill(${b.bookingId})">View Folio</button>
          <button class="btn btn-danger btn-sm" onclick="promptCheckout(${b.bookingId})">Check Out</button>
        </div>
      </td>
    </tr>
  `).join('');
}

// Render Interactive Room Grid
function renderRooms() {
  const container = document.getElementById('roomsGridContainer');
  if (!container) return;

  const roomsToRender = state.filteredRooms;

  if (roomsToRender.length === 0) {
    container.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 4rem; color: var(--text-muted); background: var(--bg-card); border-radius: var(--radius-lg);">
        <h3>No rooms match your filter</h3>
        <p style="margin-top: 0.5rem;">Try adjusting your status or category criteria.</p>
      </div>
    `;
    return;
  }

  const amenitiesMap = {
    Single: ['Single Bed', 'Wi-Fi 500Mbps', 'AC', 'Work Desk'],
    Double: ['Queen Bed', 'Wi-Fi 500Mbps', 'AC', 'Mini Fridge', 'City View'],
    Deluxe: ['King Bed', 'High-speed Wi-Fi', 'Balcony', 'Smart TV 55"', 'En-suite Tub'],
    Suite: ['Master Suite', 'Living Area', 'Panoramic Skyline', 'Jacuzzi', 'Butler Service'],
    Family: ['2 Queen Beds', 'Connecting Rooms', 'Dining Area', 'Kids Play Area', 'Kitchenette'],
  };

  container.innerHTML = roomsToRender.map((r) => {
    const isAvailable = r.status.toLowerCase() === 'available';
    const amenities = (r.amenities && Array.isArray(r.amenities) && r.amenities.length > 0)
      ? r.amenities
      : (amenitiesMap[r.roomType] || ['Wi-Fi', 'Air Conditioning', 'Room Service']);

    return `
      <div class="room-card" data-room-id="${r.roomId}">
        <div class="room-card-header">
          <div class="room-number-wrap">
            <span class="room-label">Suite</span>
            <span class="room-number">${escapeHtml(r.roomNumber)}</span>
          </div>
          <span class="room-badge ${isAvailable ? 'available' : 'booked'}">
            ${isAvailable ? '● Available' : '● Occupied'}
          </span>
        </div>

        <div class="room-card-body">
          <h3 class="room-type-title">${escapeHtml(r.roomType)} Suite</h3>
          
          <div class="room-amenities">
            ${amenities.map(a => `<span class="amenity-chip">✓ ${a}</span>`).join('')}
          </div>

          <div class="room-pricing">
            <span class="room-price-val">${formatCurrency(r.pricePerNight)}</span>
            <span class="room-price-unit">/ night + GST</span>
          </div>
        </div>

        <div class="room-card-footer">
          ${isAvailable ? `
            <button class="btn btn-primary" onclick="openBookingModalForRoom(${r.roomId})">
              🛎️ Book This Suite
            </button>
          ` : `
            <button class="btn btn-secondary" onclick="lookupBookingByRoom(${r.roomId})">
              🧾 Active Guest & Folio
            </button>
          `}
        </div>
      </div>
    `;
  }).join('');
}

// Render Reservations Table
function renderBookingsTable() {
  const tbody = document.getElementById('bookingsMainTableBody');
  if (!tbody) return;

  const bookingsList = state.showingHistory ? state.allBookings : state.activeBookings;

  if (bookingsList.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="9" style="text-align: center; padding: 3rem; color: var(--text-muted);">
          ${state.showingHistory ? 'No historical bookings found.' : 'No active in-house reservations.'}
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = bookingsList.map((b) => {
    const isBooked = b.status === 'Booked';

    return `
      <tr>
        <td><strong style="color: var(--text-gold);">#${b.bookingId}</strong></td>
        <td>
          <span class="guest-avatar">${b.guestName.charAt(0).toUpperCase()}</span>
          <strong>${escapeHtml(b.guestName)}</strong>
        </td>
        <td>${escapeHtml(b.guestPhone || '—')}</td>
        <td><strong>${escapeHtml(b.roomNumber)}</strong></td>
        <td><span class="amenity-chip">${escapeHtml(b.roomType)}</span></td>
        <td>${b.numberOfNights} night(s)</td>
        <td><strong style="color: var(--text-gold);">${formatCurrency(b.totalAmount)}</strong></td>
        <td>
          <span class="table-badge ${isBooked ? 'booked' : 'checked-out'}">
            ${isBooked ? 'Occupied' : 'Checked Out'}
          </span>
        </td>
        <td>
          <div class="table-actions">
            <button class="btn btn-secondary btn-sm" onclick="viewBill(${b.bookingId})">
              Folio
            </button>
            ${isBooked ? `
              <button class="btn btn-danger btn-sm" onclick="promptCheckout(${b.bookingId})">
                Check Out
              </button>
            ` : ''}
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

// Render Guest Directory Table
function renderGuestsTable() {
  const tbody = document.getElementById('guestsTableBody');
  if (!tbody) return;

  const guestsToRender = state.filteredGuests;

  if (guestsToRender.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6" style="text-align: center; padding: 3rem; color: var(--text-muted);">
          No guests found matching search.
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = guestsToRender.map((g) => `
    <tr>
      <td><span style="color: var(--text-muted); font-weight: 700;">#${g.guestId}</span></td>
      <td>
        <span class="guest-avatar">${g.fullName.charAt(0).toUpperCase()}</span>
        <strong>${escapeHtml(g.fullName)}</strong>
      </td>
      <td>${escapeHtml(g.phone || '—')}</td>
      <td>${escapeHtml(g.email || '—')}</td>
      <td><span style="color: var(--text-secondary);">${escapeHtml(g.address || '—')}</span></td>
      <td>
        <button class="btn btn-primary btn-sm" onclick="openBookingModalForGuest(${g.guestId})">
          + Book Stay
        </button>
      </td>
    </tr>
  `).join('');
}

// =============================================================================
// FILTERING & SEARCH CONTROLS
// =============================================================================

function filterRooms(status) {
  state.currentRoomFilter = status;

  document.querySelectorAll('[data-room-filter]').forEach((btn) => {
    btn.classList.toggle('active', btn.getAttribute('data-room-filter') === status);
  });

  applyRoomFilters();
}

function filterRoomsByType(type) {
  state.currentRoomTypeFilter = type;
  applyRoomFilters();
}

function searchRooms(keyword) {
  state.currentRoomSearch = keyword.trim().toLowerCase();
  applyRoomFilters();
}

function applyRoomFilters() {
  state.filteredRooms = state.rooms.filter((r) => {
    // Status filter
    if (state.currentRoomFilter !== 'all' && r.status !== state.currentRoomFilter) {
      return false;
    }
    // Type filter
    if (state.currentRoomTypeFilter !== 'all' && r.roomType !== state.currentRoomTypeFilter) {
      return false;
    }
    // Search keyword
    if (state.currentRoomSearch) {
      const matchNumber = r.roomNumber.toLowerCase().includes(state.currentRoomSearch);
      const matchType = r.roomType.toLowerCase().includes(state.currentRoomSearch);
      if (!matchNumber && !matchType) return false;
    }
    return true;
  });

  renderRooms();
}

function searchGuests(keyword) {
  const kw = keyword.trim().toLowerCase();
  if (!kw) {
    state.filteredGuests = [...state.guests];
  } else {
    state.filteredGuests = state.guests.filter((g) =>
      g.fullName.toLowerCase().includes(kw) ||
      (g.phone && g.phone.toLowerCase().includes(kw)) ||
      (g.email && g.email.toLowerCase().includes(kw)) ||
      (g.address && g.address.toLowerCase().includes(kw))
    );
  }
  renderGuestsTable();
}

function searchBookings(keyword) {
  const kw = keyword.trim().toLowerCase();
  const source = state.showingHistory ? state.allBookings : state.activeBookings;
  if (!kw) {
    renderBookingsTable();
    return;
  }
  const filtered = source.filter((b) =>
    b.guestName.toLowerCase().includes(kw) ||
    b.roomNumber.toLowerCase().includes(kw) ||
    (b.guestPhone && b.guestPhone.toLowerCase().includes(kw)) ||
    String(b.bookingId).includes(kw)
  );

  const tbody = document.getElementById('bookingsMainTableBody');
  if (tbody) {
    if (filtered.length === 0) {
      tbody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding: 2rem;">No matches found.</td></tr>`;
    } else {
      tbody.innerHTML = filtered.map((b) => `
        <tr>
          <td><strong style="color: var(--text-gold);">#${b.bookingId}</strong></td>
          <td><strong>${escapeHtml(b.guestName)}</strong></td>
          <td>${escapeHtml(b.guestPhone || '—')}</td>
          <td>${escapeHtml(b.roomNumber)}</td>
          <td>${escapeHtml(b.roomType)}</td>
          <td>${b.numberOfNights} night(s)</td>
          <td><strong>${formatCurrency(b.totalAmount)}</strong></td>
          <td><span class="table-badge ${b.status === 'Booked' ? 'booked' : 'checked-out'}">${b.status}</span></td>
          <td>
            <button class="btn btn-secondary btn-sm" onclick="viewBill(${b.bookingId})">Folio</button>
          </td>
        </tr>
      `).join('');
    }
  }
}

function toggleBookingsHistory(showHistory) {
  state.showingHistory = showHistory;
  document.getElementById('filterActiveBookingsBtn').classList.toggle('active', !showHistory);
  document.getElementById('filterAllHistoryBtn').classList.toggle('active', showHistory);
  renderBookingsTable();
}

// =============================================================================
// MODAL CONTROLLERS & FORMS
// =============================================================================

function openModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.showModal();
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.close();
}

// --- BOOKING WIZARD ---
async function openBookingModal(preselectedRoomId = null, preselectedGuestId = null) {
  populateGuestDropdown(preselectedGuestId);
  populateRoomDropdown(preselectedRoomId);

  // Suggest next ID from DB
  try {
    const res = await fetch(`${API_BASE}/bookings/next-id`);
    if (res.ok) {
      const data = await res.json();
      document.getElementById('bookingIdInput').value = data.nextBookingId || 101;
    }
  } catch (err) {
    document.getElementById('bookingIdInput').value = 101;
  }

  document.getElementById('bookingNightsInput').value = 1;
  updateBookingCalc();
  openModal('bookingModal');
}

function openBookingModalForRoom(roomId) {
  openBookingModal(roomId, null);
}

function openBookingModalForGuest(guestId) {
  openBookingModal(null, guestId);
}

function populateGuestDropdown(selectedId = null) {
  const sel = document.getElementById('bookingGuestSelect');
  if (!sel) return;

  sel.innerHTML = '<option value="">-- Choose Registered Guest --</option>' +
    state.guests.map((g) => `
      <option value="${g.guestId}" ${selectedId == g.guestId ? 'selected' : ''}>
        ${escapeHtml(g.fullName)} (ID #${g.guestId}) - ${escapeHtml(g.phone || 'No phone')}
      </option>
    `).join('');
}

function populateRoomDropdown(selectedId = null) {
  const sel = document.getElementById('bookingRoomSelect');
  if (!sel) return;

  const availableRooms = state.rooms.filter(r => r.status.toLowerCase() === 'available');

  sel.innerHTML = '<option value="">-- Choose Available Suite --</option>' +
    availableRooms.map((r) => `
      <option value="${r.roomId}" data-price="${r.pricePerNight}" ${selectedId == r.roomId ? 'selected' : ''}>
        Room ${escapeHtml(r.roomNumber)} (${escapeHtml(r.roomType)}) — ${formatCurrency(r.pricePerNight)}/night
      </option>
    `).join('');
}

function updateBookingCalc() {
  const roomSelect = document.getElementById('bookingRoomSelect');
  const nightsInput = document.getElementById('bookingNightsInput');

  const selectedOpt = roomSelect.options[roomSelect.selectedIndex];
  const rate = selectedOpt && selectedOpt.dataset.price ? Number(selectedOpt.dataset.price) : 0;
  const nights = Math.max(1, parseInt(nightsInput.value, 10) || 1);

  const subtotal = rate * nights;
  const tax = Math.round(subtotal * 0.12 * 100) / 100;
  const grandTotal = subtotal + tax;

  document.getElementById('calcRoomRate').textContent = `${formatCurrency(rate)} / night`;
  document.getElementById('calcNightsCount').textContent = `${nights} night(s)`;
  document.getElementById('calcSubtotal').textContent = formatCurrency(subtotal);
  document.getElementById('calcTax').textContent = formatCurrency(tax);
  document.getElementById('calcGrandTotal').textContent = formatCurrency(grandTotal);
}

async function submitBooking(event) {
  event.preventDefault();
  const btn = document.getElementById('confirmBookingBtn');
  btn.disabled = true;
  btn.textContent = 'Processing Reservation...';

  const bookingId = document.getElementById('bookingIdInput').value;
  const guestId = document.getElementById('bookingGuestSelect').value;
  const roomId = document.getElementById('bookingRoomSelect').value;
  const numberOfNights = document.getElementById('bookingNightsInput').value;

  try {
    const res = await fetch(`${API_BASE}/bookings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookingId, guestId, roomId, numberOfNights }),
    });

    const data = await res.json();
    if (!res.ok || data.success === false) {
      throw new Error(data.error || 'Failed to book room');
    }

    showToast(`Reservation #${data.bookingId} created successfully!`, 'success');
    closeModal('bookingModal');
    document.getElementById('bookingForm').reset();

    await fetchAllData();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Confirm Reservation';
  }
}

// --- ADD GUEST ---
function openAddGuestModal() {
  document.getElementById('addGuestForm').reset();
  openModal('addGuestModal');
}

async function submitGuest(event) {
  event.preventDefault();
  const btn = document.getElementById('saveGuestBtn');
  btn.disabled = true;
  btn.textContent = 'Registering Guest...';

  const fullName = document.getElementById('guestFullName').value;
  const phone = document.getElementById('guestPhone').value;
  const email = document.getElementById('guestEmail').value;
  const address = document.getElementById('guestAddress').value;

  try {
    const res = await fetch(`${API_BASE}/guests`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fullName, phone, email, address }),
    });

    const data = await res.json();
    if (!res.ok || data.success === false) {
      throw new Error(data.error || 'Failed to save guest');
    }

    showToast(`Guest "${fullName}" registered with ID #${data.guestId}!`, 'success');
    closeModal('addGuestModal');
    document.getElementById('addGuestForm').reset();

    await fetchGuests();
    await fetchStats();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Save & Register Guest';
  }
}

// --- ADD ROOM ---
function openAddRoomModal() {
  document.getElementById('addRoomForm').reset();
  openModal('addRoomModal');
}

async function submitRoom(event) {
  event.preventDefault();
  const roomNumber = document.getElementById('newRoomNumber').value;
  const roomType = document.getElementById('newRoomType').value;
  const pricePerNight = document.getElementById('newRoomPrice').value;

  try {
    const res = await fetch(`${API_BASE}/rooms`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ roomNumber, roomType, pricePerNight }),
    });

    const data = await res.json();
    if (!res.ok || data.success === false) {
      throw new Error(data.error || 'Failed to add room');
    }

    showToast(`Room ${roomNumber} (${roomType}) added to inventory!`, 'success');
    closeModal('addRoomModal');
    document.getElementById('addRoomForm').reset();

    await fetchAllData();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// =============================================================================
// BILLING, INVOICE & CHECKOUT
// =============================================================================

function populateBillingLookupDropdown() {
  const sel = document.getElementById('lookupBookingSelect');
  if (!sel) return;

  sel.innerHTML = '<option value="">-- Choose Active In-House Stay --</option>' +
    state.activeBookings.map((b) => `
      <option value="${b.bookingId}">
        Booking #${b.bookingId} - ${escapeHtml(b.guestName)} (Room ${b.roomNumber})
      </option>
    `).join('');
}

function lookupBookingByRoom(roomId) {
  const booking = state.activeBookings.find(b => b.roomId == roomId);
  if (booking) {
    viewBill(booking.bookingId);
  } else {
    showToast('No active booking found for this room.', 'info');
  }
}

async function viewBill(bookingId) {
  try {
    const res = await fetch(`${API_BASE}/bill?bookingId=${bookingId}`);
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.error || 'Unable to retrieve bill');
    }

    const bill = await res.json();
    displayInvoiceModal(bill);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function displayInvoiceModal(bill) {
  state.activeInvoiceBookingId = bill.bookingId;

  document.getElementById('invoiceNumber').textContent = `FOLIO #GH-${bill.bookingId}`;
  document.getElementById('invoiceGuestName').textContent = bill.guestName;
  document.getElementById('invoiceGuestPhone').textContent = `Phone: ${bill.guestPhone || 'N/A'}`;
  document.getElementById('invoiceGuestEmail').textContent = `Email: ${bill.guestEmail || 'N/A'}`;
  document.getElementById('invoiceGuestAddress').textContent = `Address: ${bill.guestAddress || 'N/A'}`;

  document.getElementById('invoiceRoomNumber').textContent = bill.roomNumber;
  document.getElementById('invoiceRoomType').textContent = bill.roomType;
  document.getElementById('invoiceNights').textContent = bill.numberOfNights;
  document.getElementById('invoiceDate').textContent = bill.bookingDate || new Date().toLocaleString();

  document.getElementById('invoiceItemDesc').textContent = `${bill.roomType} Suite Stay Charges`;
  document.getElementById('invoiceItemRate').textContent = formatCurrency(bill.pricePerNight);
  document.getElementById('invoiceItemNights').textContent = bill.numberOfNights;
  document.getElementById('invoiceItemSubtotal').textContent = formatCurrency(bill.subtotal);

  const halfTax = Math.round((bill.taxAmount / 2) * 100) / 100;
  document.getElementById('invoiceSubtotalVal').textContent = formatCurrency(bill.subtotal);

  const discRow = document.getElementById('invoiceDiscountRow');
  const discVal = document.getElementById('invoiceDiscountVal');
  const discLbl = document.getElementById('invoiceDiscountLabel');
  if (discRow && discVal) {
    if (bill.discount && bill.discount > 0) {
      discRow.style.display = 'flex';
      discVal.textContent = '-' + formatCurrency(bill.discount);
      if (discLbl && bill.strategyName) {
        discLbl.textContent = `Discount (${bill.strategyName}):`;
      }
    } else {
      discRow.style.display = 'none';
    }
  }

  document.getElementById('invoiceCgstVal').textContent = formatCurrency(halfTax);
  document.getElementById('invoiceSgstVal').textContent = formatCurrency(halfTax);
  document.getElementById('invoiceGrandTotalVal').textContent = formatCurrency(bill.grandTotal);

  const isCheckedOut = bill.status && bill.status.toLowerCase() === 'checked out';
  const badge = document.getElementById('invoiceStatusBadge');
  const checkoutBtn = document.getElementById('invoiceCheckoutActionBtn');

  if (isCheckedOut) {
    badge.textContent = 'SETTLED & CHECKED OUT';
    badge.className = 'invoice-badge-status paid';
    checkoutBtn.style.display = 'none';
  } else {
    badge.textContent = 'ACTIVE FOLIO';
    badge.className = 'invoice-badge-status unpaid';
    checkoutBtn.style.display = 'inline-flex';
  }

  openModal('invoiceModal');
}

function printInvoice() {
  window.print();
}

async function promptCheckout(bookingId) {
  state.activeInvoiceBookingId = bookingId;
  await viewBill(bookingId);
}

async function executeCheckoutFromInvoice() {
  if (!state.activeInvoiceBookingId) return;

  const bookingId = state.activeInvoiceBookingId;
  const btn = document.getElementById('invoiceCheckoutActionBtn');
  btn.disabled = true;
  btn.textContent = 'Completing Checkout...';

  try {
    const res = await fetch(`${API_BASE}/checkout`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookingId }),
    });

    const data = await res.json();
    if (!res.ok || data.success === false) {
      throw new Error(data.error || 'Failed to complete checkout');
    }

    showToast(`Booking #${bookingId} checked out successfully! Total: ${formatCurrency(data.invoice.grandTotal)}`, 'success');
    displayInvoiceModal(data.invoice);

    await fetchAllData();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Confirm Checkout & Release Room';
  }
}

async function handleBillingLookup(event) {
  event.preventDefault();
  const idInput = document.getElementById('lookupBookingId');
  const bookingId = idInput.value;
  if (!bookingId) return;

  await viewBill(bookingId);
}

async function handleDirectCheckout() {
  const idInput = document.getElementById('lookupBookingId');
  const bookingId = idInput.value;
  if (!bookingId) {
    showToast('Please enter or select a Booking ID first.', 'info');
    return;
  }

  await promptCheckout(bookingId);
}

// =============================================================================
// UTILITIES & TOAST ALERTS
// =============================================================================

function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;

  const icon = type === 'success' ? '✓' : type === 'error' ? '⚠️' : 'ℹ️';
  toast.innerHTML = `<span style="font-size: 1.1rem;">${icon}</span> <span>${escapeHtml(message)}</span>`;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
