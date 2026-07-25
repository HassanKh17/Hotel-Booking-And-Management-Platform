import { useEffect, useState } from "react";
import { BedDouble, CalendarDays, RefreshCw } from "lucide-react";
import { getOccupancyDashboard } from "../services/ownerService";
import {
  Badge,
  BackButton,
  Button,
  Card,
  DataTable,
  EmptyState,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import { formatDate, formatRoomType } from "../utils/formatters";

function OwnerOccupancyDashboard() {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getOccupancyDashboard();
      setHotels(data);
    } catch (err) {
      setError("Failed to load occupancy dashboard.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const totalRooms = hotels.reduce((sum, hotel) => sum + Number(hotel.totalRooms ?? 0), 0);
  const occupiedToday = hotels.reduce((sum, hotel) => sum + Number(hotel.occupiedToday ?? 0), 0);
  const futureBookings = hotels.reduce((sum, hotel) => sum + Number(hotel.futureBookings ?? 0), 0);

  const renderRoomTable = (rooms, label) => {
    if (!rooms?.length) {
      return <EmptyState title={`No ${label.toLowerCase()}`} description="There are no rooms in this category." />;
    }

    return (
      <DataTable aria-label={label}>
        <thead>
          <tr>
            <th>Room ID</th>
            <th>Type</th>
            <th>Check-in</th>
            <th>Check-out</th>
          </tr>
        </thead>
        <tbody>
          {rooms.map((room) => (
            <tr key={room.bookingItemId}>
              <td>{room.roomId}</td>
              <td>{formatRoomType(room.occupancyType)}</td>
              <td>{formatDate(room.checkIn)}</td>
              <td>{formatDate(room.checkOut)}</td>
            </tr>
          ))}
        </tbody>
      </DataTable>
    );
  };

  return (
    <PageShell
      className="owner-occupancy-page"
      eyebrow="Owner analytics"
      title="Occupancy dashboard"
      description="View current occupancy, availability, and upcoming bookings across your hotels."
      toolbar={<BackButton to="/hotel-owner/hotels" label="Back to hotels" />}
      actions={
        <Button variant="secondary" icon={<RefreshCw size={17} />} onClick={fetchDashboard}>
          Refresh
        </Button>
      }
    >
      <div className="ui-stat-grid">
        <div className="ui-stat-card">
          <span>Total rooms</span>
          <strong>{totalRooms}</strong>
        </div>
        <div className="ui-stat-card">
          <span>Occupied today</span>
          <strong>{occupiedToday}</strong>
        </div>
        <div className="ui-stat-card">
          <span>Future bookings</span>
          <strong>{futureBookings}</strong>
        </div>
      </div>

      {loading ? (
        <LoadingState label="Loading dashboard..." />
      ) : error ? (
        <EmptyState title="Dashboard could not be loaded" description={error} />
      ) : hotels.length === 0 ? (
        <Card>
          <EmptyState title="No hotels found" description="Add hotels before viewing occupancy analytics." />
        </Card>
      ) : (
        <div className="occupancy-card-grid">
          {hotels.map((hotel) => (
            <Card key={hotel.hotelId} className="occupancy-card">
              <div className="occupancy-card__header">
                <div>
                  <h2>{hotel.hotelName}</h2>
                  <p>Hotel #{hotel.hotelId}</p>
                </div>
                <div className="occupancy-card__status">
                  <Badge tone={hotel.occupancyPercentage > 80 ? "warning" : "success"}>
                    {hotel.occupancyPercentage.toFixed(1)}% occupied
                  </Badge>
                </div>
              </div>

              <div className="occupancy-card__body">
                <div className="occupancy-kpi-grid">
                  <div className="occupancy-kpi">
                    <span>Total rooms</span>
                    <strong>{hotel.totalRooms}</strong>
                  </div>
                  <div className="occupancy-kpi">
                    <span>Occupied today</span>
                    <strong>{hotel.occupiedToday}</strong>
                  </div>
                  <div className="occupancy-kpi">
                    <span>Available today</span>
                    <strong>{hotel.availableToday}</strong>
                  </div>
                  <div className="occupancy-kpi">
                    <span>Future bookings</span>
                    <strong>{hotel.futureBookings}</strong>
                  </div>
                </div>

                <div className="occupancy-meter">
                  <div className="room-card__meta">
                    <strong>Occupancy</strong>
                    <span>{hotel.occupancyPercentage.toFixed(1)}%</span>
                  </div>
                  <div className="progress-meter">
                    <div
                      className="progress-meter__bar"
                      style={{ width: `${Math.min(hotel.occupancyPercentage, 100)}%` }}
                    />
                  </div>
                </div>

                <div className="occupancy-section">
                  <SectionHeader
                    title="Currently occupied"
                    description="Rooms that are booked today."
                    actions={<BedDouble size={18} />}
                  />
                  {renderRoomTable(hotel.currentBookedRooms, "Currently occupied rooms")}
                </div>

                <div className="occupancy-section">
                  <SectionHeader
                    title="Upcoming bookings"
                    description="Future stays already on the calendar."
                    actions={<CalendarDays size={18} />}
                  />
                  {renderRoomTable(hotel.upcomingBookedRooms, "Upcoming booked rooms")}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </PageShell>
  );
}

export default OwnerOccupancyDashboard;
