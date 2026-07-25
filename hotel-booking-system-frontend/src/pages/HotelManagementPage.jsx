import { useEffect, useState } from "react";
import { BarChart3, Edit3, Eye, FileText, Plus, Search, Trash2 } from "lucide-react";
import { useNavigate } from "react-router-dom";
import HotelForm from "../components/hotels/HotelForm";
import { getMyHotels, createHotel, updateHotel, deleteHotel } from "../services/hotelService";
import {
  Alert,
  Badge,
  Button,
  Card,
  ConfirmDialog,
  EmptyState,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import {
  formatCurrency,
  getHotelImageUrl,
  getLowestRoomPrice,
  truncateText,
} from "../utils/formatters";
import "../styles/HotelManagementPage.css";

function HotelManagementPage() {
  const [hotels, setHotels] = useState([]);
  const [selectedHotel, setSelectedHotel] = useState(null);
  const [hotelToDelete, setHotelToDelete] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();

  const fetchHotels = async () => {
    try {
      setPageLoading(true);
      setError("");
      const data = await getMyHotels();
      setHotels(data);
    } catch (err) {
      setError("Failed to fetch hotels.");
    } finally {
      setPageLoading(false);
    }
  };

  useEffect(() => {
    fetchHotels();
  }, []);

  const handleAdd = () => {
    setSelectedHotel(null);
    setIsModalOpen(true);
  };

  const handleEdit = (hotel) => {
    setSelectedHotel(hotel);
    setIsModalOpen(true);
  };

  const handleClose = () => {
    setIsModalOpen(false);
    setSelectedHotel(null);
  };

  const handleSubmit = async (formData) => {
    setLoading(true);
    setError("");
    try {
      if (selectedHotel?.hotelId) {
        await updateHotel(selectedHotel.hotelId, formData);
      } else {
        await createHotel(formData);
      }
      await fetchHotels();
      handleClose();
    } catch (err) {
      setError("Failed to save hotel.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!hotelToDelete) return;
    setError("");
    try {
      await deleteHotel(hotelToDelete.hotelId);
      setHotelToDelete(null);
      await fetchHotels();
    } catch (err) {
      setError("Failed to delete hotel.");
    }
  };

  const filteredHotels = hotels.filter((hotel) => {
    const term = searchTerm.toLowerCase();
    return hotel.name?.toLowerCase().includes(term) || String(hotel.hotelId).includes(term);
  });

  const totalRooms = hotels.reduce((sum, hotel) => sum + (hotel.rooms?.length ?? 0), 0);
  const availableRooms = hotels.reduce(
    (sum, hotel) => sum + (hotel.rooms?.filter((room) => room.occupancyStatus === "AVAILABLE").length ?? 0),
    0
  );

  return (
    <PageShell
      className="hotel-page"
      eyebrow="Owner portal"
      title="Hotel management"
      description="Manage your properties, room inventory, occupancy view, and account statement."
      actions={
        <>
          <Button to="/hotel-owner/occupancy-dashboard" variant="secondary" icon={<BarChart3 size={17} />}>
            Occupancy
          </Button>
          <Button to="/hotel-owner/statement" variant="secondary" icon={<FileText size={17} />}>
            Statement
          </Button>
          <Button icon={<Plus size={17} />} onClick={handleAdd}>
            Add hotel
          </Button>
        </>
      }
    >
      <div className="ui-stat-grid">
        <div className="ui-stat-card">
          <span>Total hotels</span>
          <strong>{hotels.length}</strong>
        </div>
        <div className="ui-stat-card">
          <span>Total rooms</span>
          <strong>{totalRooms}</strong>
        </div>
        <div className="ui-stat-card">
          <span>Available rooms</span>
          <strong>{availableRooms}</strong>
        </div>
      </div>

      <Card>
        <SectionHeader
          title="Your hotels"
          description="Search by name or ID, then open details or make changes."
          actions={
            <div className="hotel-filter owner-search">
              <label htmlFor="hotelSearch">
                <Search size={14} /> Search hotels
              </label>
              <input
                id="hotelSearch"
                type="search"
                placeholder="Name or ID"
                value={searchTerm}
                onChange={(event) => setSearchTerm(event.target.value)}
              />
            </div>
          }
        />

        {error && <Alert type="error">{error}</Alert>}

        {pageLoading ? (
          <LoadingState label="Loading hotels..." />
        ) : filteredHotels.length === 0 ? (
          <EmptyState
            title="No hotels found"
            description="Add a hotel or adjust your search term."
            actions={<Button onClick={handleAdd}>Add hotel</Button>}
          />
        ) : (
          <div className="dashboard-card-grid">
            {filteredHotels.map((hotel) => {
              const imageUrl = getHotelImageUrl(hotel.photoUrl);
              const lowestPrice = getLowestRoomPrice(hotel.rooms);
              const hotelAvailableRooms =
                hotel.rooms?.filter((room) => room.occupancyStatus === "AVAILABLE").length ?? 0;

              return (
                <article className="hotel-card" key={hotel.hotelId}>
                  <div className="hotel-card__media">
                    <div className="hotel-image-placeholder">Hotel image coming soon</div>
                    {imageUrl && (
                      <img
                        src={imageUrl}
                        alt={hotel.name}
                        onError={(event) => {
                          event.currentTarget.style.display = "none";
                        }}
                      />
                    )}
                  </div>
                  <div className="hotel-card__content">
                    <div className="hotel-card__topline">
                      <Badge tone="info">Hotel #{hotel.hotelId}</Badge>
                      <Badge tone={hotelAvailableRooms > 0 ? "success" : "neutral"}>
                        {hotelAvailableRooms} available
                      </Badge>
                    </div>
                    <div>
                      <h3>{hotel.name}</h3>
                      <p>{truncateText(hotel.description || "No description provided.", 110)}</p>
                    </div>
                    <div className="hotel-card__topline">
                      <span className="ui-meta">{hotel.address?.city ?? "Location TBC"}</span>
                      <strong>{lowestPrice ? formatCurrency(lowestPrice) : "Price TBC"}</strong>
                    </div>
                    <div className="ui-actions">
                      <Button
                        variant="secondary"
                        size="sm"
                        icon={<Eye size={14} />}
                        onClick={() => navigate(`/hotel-owner/hotels/${hotel.hotelId}`)}
                      >
                        View
                      </Button>
                      <Button variant="secondary" size="sm" icon={<Edit3 size={14} />} onClick={() => handleEdit(hotel)}>
                        Edit
                      </Button>
                      <Button variant="danger" size="sm" icon={<Trash2 size={14} />} onClick={() => setHotelToDelete(hotel)}>
                        Delete
                      </Button>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </Card>

      <HotelForm
        isOpen={isModalOpen}
        onClose={handleClose}
        onSubmit={handleSubmit}
        initialData={selectedHotel}
        loading={loading}
      />

      <ConfirmDialog
        open={Boolean(hotelToDelete)}
        title="Delete hotel?"
        message={`Delete ${hotelToDelete?.name ?? "this hotel"}? This action cannot be undone.`}
        confirmLabel="Delete"
        onCancel={() => setHotelToDelete(null)}
        onConfirm={handleDelete}
      />
    </PageShell>
  );
}

export default HotelManagementPage;
