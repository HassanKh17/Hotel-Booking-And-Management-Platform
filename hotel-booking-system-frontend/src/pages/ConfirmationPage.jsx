import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { CalendarCheck2, Hotel } from "lucide-react";
import { Button, Card, PageShell } from "../components/ui";

const ConfirmationPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!location.state?.fromCheckout) {
      navigate("/hotels");
    }
  }, [location, navigate]);

  return (
    <PageShell className="confirmation-page" narrow>
      <div className="booking-stepper" aria-label="Booking progress">
        <div className="booking-step">
          <span>1</span> Basket
        </div>
        <div className="booking-step">
          <span>2</span> Payment
        </div>
        <div className="booking-step active">
          <span>3</span> Confirmation
        </div>
      </div>

      <Card className="confirmation-card">
        <div className="confirmation-card__icon">
          <CalendarCheck2 size={42} />
        </div>
        <h1>Booking confirmed</h1>
        <p>Your reservation has been processed. You can review booking details from your account at any time.</p>
        <div className="ui-actions confirmation-card__actions">
          <Button to="/bookings" icon={<CalendarCheck2 size={17} />}>
            View my bookings
          </Button>
          <Button to="/hotels" variant="secondary" icon={<Hotel size={17} />}>
            Browse hotels
          </Button>
        </div>
      </Card>
    </PageShell>
  );
};

export default ConfirmationPage;
