import { useState, useEffect } from "react";
import "../../styles/HotelFormModal.css";
import { Alert } from "../ui";

const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api/v1";

const EMPTY_ROOM = {
    occupancyType: "SINGLE",
    occupancyStatus: "AVAILABLE",
    numberOfRooms: 1,
    capacity: 1,
    pricePerNight: 0,
};

const buildAddressPayload = (address = {}) => ({
    street: address.street ?? "",
    city: address.city ?? "",
    postcode: address.postcode ?? "",
});

const buildRoomPayload = (room, isEditMode) => ({
    occupancyType: room.occupancyType,
    occupancyStatus: room.occupancyStatus,
    numberOfRooms: isEditMode ? 1 : Number(room.numberOfRooms || 1),
    capacity: Number(room.capacity || 1),
    pricePerNight: Number(room.pricePerNight || 0),
});

const HotelForm = ({ isOpen, onClose, onSubmit, initialData = {}, loading }) => {
    const [hotel, setHotel] = useState({
        name: "",
        description: "",
        specialOfferPercentage: "",
        address: { street: "", city: "", postcode: "" },
        rooms: [{ ...EMPTY_ROOM }],
        image: null,
    });
    const [formError, setFormError] = useState("");
    const [allAmenities, setAllAmenities] = useState([]);
    const [allFacilities, setAllFacilities] = useState([]);
    const [selectedAmenities, setSelectedAmenities] = useState([]);
    const [selectedFacilities, setSelectedFacilities] = useState([]);

    const isEditMode = Boolean(initialData?.hotelId);

    useEffect(() => {
        fetch(`${API_BASE_URL}/amenities`)
            .then((res) => res.json())
            .then((data) => setAllAmenities(data || []));

        fetch(`${API_BASE_URL}/facilities`)
            .then((res) => res.json())
            .then((data) => setAllFacilities(data || []));
    }, []);

    useEffect(() => {
        if (initialData && Object.keys(initialData).length > 0) {
            setHotel({
                name: initialData.name || "",
                description: initialData.description || "",
                specialOfferPercentage: initialData.specialOfferPercentage ?? "",
                address: buildAddressPayload(initialData.address),
                rooms: initialData.rooms?.length
                    ? initialData.rooms.map((room) => ({
                        ...EMPTY_ROOM,
                        occupancyType: room.occupancyType || EMPTY_ROOM.occupancyType,
                        occupancyStatus: room.occupancyStatus || EMPTY_ROOM.occupancyStatus,
                        numberOfRooms: room.numberOfRooms || 1,
                        capacity: room.capacity || 1,
                        pricePerNight: room.pricePerNight || 0,
                    }))
                    : [{ ...EMPTY_ROOM }],
                image: null,
            });

            setSelectedAmenities(initialData.amenities?.map((a) => a.amenityId) || []);
            setSelectedFacilities(initialData.facilities?.map((f) => f.facilityId) || []);
        } else {
            setHotel({
                name: "",
                description: "",
                specialOfferPercentage: "",
                address: { street: "", city: "", postcode: "" },
                rooms: [{ ...EMPTY_ROOM }],
                image: null,
            });
            setSelectedAmenities([]);
            setSelectedFacilities([]);
        }
        setFormError("");
    }, [initialData]);

    if (!isOpen) return null;

    const handleChange = (event) => {
        const { name, value } = event.target;
        setHotel((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleAddressChange = (field, value) => {
        setHotel((prev) => ({
            ...prev,
            address: { ...prev.address, [field]: value },
        }));
    };

    const handleFileChange = (event) => {
        setHotel((prev) => ({ ...prev, image: event.target.files[0] }));
    };

    const toggleAmenity = (id) => {
        setSelectedAmenities((prev) =>
            prev.includes(id) ? prev.filter((a) => a !== id) : [...prev, id]
        );
    };

    const toggleFacility = (id) => {
        setSelectedFacilities((prev) =>
            prev.includes(id) ? prev.filter((f) => f !== id) : [...prev, id]
        );
    };

    const handleRoomChange = (index, field, value) => {
        const updatedRooms = [...hotel.rooms];
        updatedRooms[index] = {
            ...updatedRooms[index],
            [field]: ["numberOfRooms", "capacity", "pricePerNight"].includes(field)
                ? Number(value)
                : value,
        };
        setHotel((prev) => ({ ...prev, rooms: updatedRooms }));
    };

    const addRoom = () => {
        setHotel((prev) => ({ ...prev, rooms: [...prev.rooms, { ...EMPTY_ROOM }] }));
    };

    const removeRoom = (index) => {
        setHotel((prev) => ({
            ...prev,
            rooms: prev.rooms.filter((_, i) => i !== index),
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        setFormError("");

        const offer = hotel.specialOfferPercentage === ""
            ? null
            : Number(hotel.specialOfferPercentage);

        if (offer !== null && (offer < 0 || offer > 10)) {
            setFormError("Special offer must be between 0% and 10%.");
            return;
        }

        if (!hotel.rooms.length) {
            setFormError("Add at least one room before saving the hotel.");
            return;
        }

        const formattedHotel = {
            name: hotel.name,
            description: hotel.description,
            address: buildAddressPayload(hotel.address),
            rooms: hotel.rooms.map((room) => buildRoomPayload(room, isEditMode)),
            specialOfferPercentage: offer,
        };

        const formData = new FormData();

        formData.append(
            "hotel",
            new Blob([JSON.stringify(formattedHotel)], { type: "application/json" })
        );

        formData.append(
            "amenityIds",
            new Blob([JSON.stringify(selectedAmenities)], { type: "application/json" })
        );

        formData.append(
            "facilityIds",
            new Blob([JSON.stringify(selectedFacilities)], { type: "application/json" })
        );

        if (hotel.image) {
            formData.append("image", hotel.image);
        }

        onSubmit(formData);
    };

    return (
        <div className="modal-backdrop">
            <div className="hotel-form-modal">
                <div className="hotel-form-modal__header">
                    <h2>{isEditMode ? "Edit Hotel" : "Add Hotel"}</h2>
                    <button
                        type="button"
                        className="hotel-form-modal__close"
                        onClick={onClose}
                        aria-label="Close hotel form"
                    >
                        x
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="hotel-form-modal__form">
                    {formError && <Alert type="error">{formError}</Alert>}

                    <div className="hotel-form-modal__field">
                        <label htmlFor="hotelName">Hotel name</label>
                        <input id="hotelName" name="name" value={hotel.name} onChange={handleChange} required />
                    </div>

                    <div className="hotel-form-modal__field">
                        <label htmlFor="hotelDescription">Description</label>
                        <input
                            id="hotelDescription"
                            name="description"
                            value={hotel.description}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="hotel-form-modal__field">
                        <label htmlFor="specialOfferPercentage">Special offer (% discount, max 10%)</label>
                        <input
                            id="specialOfferPercentage"
                            type="number"
                            min="0"
                            max="10"
                            step="0.1"
                            name="specialOfferPercentage"
                            value={hotel.specialOfferPercentage}
                            onChange={handleChange}
                            placeholder="e.g. 5 for 5% off"
                        />
                    </div>

                    <div className="ui-form-grid">
                        <div className="hotel-form-modal__field">
                            <label htmlFor="hotelStreet">Street</label>
                            <input
                                id="hotelStreet"
                                value={hotel.address.street}
                                onChange={(event) => handleAddressChange("street", event.target.value)}
                            />
                        </div>

                        <div className="hotel-form-modal__field">
                            <label htmlFor="hotelCity">City</label>
                            <input
                                id="hotelCity"
                                value={hotel.address.city}
                                onChange={(event) => handleAddressChange("city", event.target.value)}
                            />
                        </div>
                    </div>

                    <div className="hotel-form-modal__field">
                        <label htmlFor="hotelPostcode">Postcode</label>
                        <input
                            id="hotelPostcode"
                            value={hotel.address.postcode}
                            onChange={(event) => handleAddressChange("postcode", event.target.value)}
                        />
                    </div>

                    <div className="hotel-form-modal__field">
                        <label>Amenities</label>
                        <div className="checkbox-grid">
                            {allAmenities.map((amenity) => (
                                <label key={amenity.amenityId} className="checkbox-item">
                                    <input
                                        type="checkbox"
                                        checked={selectedAmenities.includes(amenity.amenityId)}
                                        onChange={() => toggleAmenity(amenity.amenityId)}
                                    />
                                    {amenity.name}
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="hotel-form-modal__field">
                        <label>Facilities</label>
                        <div className="checkbox-grid">
                            {allFacilities.map((facility) => (
                                <label key={facility.facilityId} className="checkbox-item">
                                    <input
                                        type="checkbox"
                                        checked={selectedFacilities.includes(facility.facilityId)}
                                        onChange={() => toggleFacility(facility.facilityId)}
                                    />
                                    {facility.name}
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="hotel-form-modal__field">
                        <label>Rooms</label>

                        {hotel.rooms.map((room, index) => (
                            <div key={`${room.occupancyType}-${index}`} className="hotel-room-editor">
                                <p className="hotel-room-editor__title">Room {index + 1}</p>

                                <div className="ui-form-grid">
                                    <div className="hotel-room-editor__field">
                                        <label>Occupancy type</label>
                                        <select
                                            value={room.occupancyType}
                                            onChange={(event) =>
                                                handleRoomChange(index, "occupancyType", event.target.value)
                                            }
                                        >
                                            <option value="SINGLE">Single</option>
                                            <option value="DOUBLE">Double</option>
                                        </select>
                                    </div>

                                    <div className="hotel-room-editor__field">
                                        <label>Occupancy status</label>
                                        <select
                                            value={room.occupancyStatus}
                                            onChange={(event) =>
                                                handleRoomChange(index, "occupancyStatus", event.target.value)
                                            }
                                        >
                                            <option value="AVAILABLE">Available</option>
                                            <option value="OCCUPIED">Occupied</option>
                                        </select>
                                    </div>
                                </div>

                                {!isEditMode && (
                                    <div className="hotel-room-editor__field">
                                        <label>Number of rooms</label>
                                        <input
                                            type="number"
                                            min="1"
                                            value={room.numberOfRooms}
                                            onChange={(event) =>
                                                handleRoomChange(index, "numberOfRooms", event.target.value)
                                            }
                                        />
                                    </div>
                                )}

                                <div className="ui-form-grid">
                                    <div className="hotel-room-editor__field">
                                        <label>Capacity</label>
                                        <input
                                            type="number"
                                            min="1"
                                            value={room.capacity}
                                            onChange={(event) =>
                                                handleRoomChange(index, "capacity", event.target.value)
                                            }
                                        />
                                    </div>

                                    <div className="hotel-room-editor__field">
                                        <label>Price (GBP)</label>
                                        <input
                                            type="number"
                                            min="0"
                                            step="0.01"
                                            value={room.pricePerNight}
                                            onChange={(event) =>
                                                handleRoomChange(index, "pricePerNight", event.target.value)
                                            }
                                        />
                                    </div>
                                </div>

                                <button
                                    type="button"
                                    onClick={() => removeRoom(index)}
                                    className="hotel-btn hotel-btn--danger hotel-room-editor__remove"
                                >
                                    Remove room
                                </button>
                            </div>
                        ))}

                        <button type="button" onClick={addRoom} className="hotel-btn hotel-btn--primary">
                            + Add room
                        </button>
                    </div>

                    <div className="hotel-form-modal__field">
                        <label htmlFor="hotelImage">Hotel image</label>
                        <input id="hotelImage" type="file" accept="image/*" onChange={handleFileChange} />
                    </div>

                    <div className="hotel-form-modal__actions">
                        <button type="button" className="hotel-btn hotel-btn--secondary" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="hotel-btn hotel-btn--primary" disabled={loading}>
                            {loading ? "Saving..." : isEditMode ? "Save Changes" : "Create Hotel"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default HotelForm;
