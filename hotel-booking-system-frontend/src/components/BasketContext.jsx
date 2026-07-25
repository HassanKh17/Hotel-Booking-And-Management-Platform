import { createContext, useContext, useState } from "react";

const BasketContext = createContext();

export function BasketProvider({ children }) {
  const [basket, setBasket] = useState([]);
  const [addedRoomIds, setAddedRoomIds] = useState([]);

  const addToBasket = (item) => {
    setBasket((prev) => [...prev, item]);
    setAddedRoomIds((prev) => [...prev, item.room.roomId]);
  };

  const removeFromBasket = (index) => {
    const roomId = basket[index].room.roomId;
    setBasket((prev) => prev.filter((_, i) => i !== index));
    setAddedRoomIds((prev) => prev.filter((id) => id !== roomId));
  };

  const isRoomAdded = (roomId) => addedRoomIds.includes(roomId);

  const clearBasket = () => {
    setBasket([]);
    setAddedRoomIds([]);
  };

  return (
    <BasketContext.Provider value={{ basket, addToBasket, removeFromBasket,
        isRoomAdded, clearBasket }}>
      {children}
    </BasketContext.Provider>
  );
}

export function useBasket() {
  return useContext(BasketContext);
}
