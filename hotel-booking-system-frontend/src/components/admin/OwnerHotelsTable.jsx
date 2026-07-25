function OwnerHotelsTable({ hotels }) {
    return (
        <div className="charges-card">
            <h2 style={{ marginBottom: "16px" }}>Hotels</h2>

            {(!hotels || hotels.length === 0) ? (
                <p>No hotels found for this owner.</p>
            ) : (
                <div className="table-responsive">
                    <table className="owner-table">
                        <thead>
                            <tr>
                                <th>Hotel ID</th>
                                <th>Name</th>
                                <th>City</th>
                                <th>Star Rating</th>
                                <th>Special Offer %</th>
                            </tr>
                        </thead>
                        <tbody>
                            {hotels.map((hotel) => (
                                <tr key={hotel.hotelId}>
                                    <td>{hotel.hotelId}</td>
                                    <td>{hotel.name}</td>
                                    <td>{hotel.city}</td>
                                    <td>{hotel.starRating}</td>
                                    <td>{hotel.specialOfferPct ?? 0}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default OwnerHotelsTable;