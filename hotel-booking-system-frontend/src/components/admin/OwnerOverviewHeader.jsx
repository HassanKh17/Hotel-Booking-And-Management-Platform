import { Link } from "react-router-dom";

function OwnerOverviewHeader() {
    return (
        <div className="charges-header">
            <div>
                <h1>Owner Overview</h1>
                <p>View owner details and all hotels registered under this owner.</p>
            </div>

            <Link to="/admin" className="back-btn">
                Back to Dashboard
            </Link>
        </div>
    );
}

export default OwnerOverviewHeader;