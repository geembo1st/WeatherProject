CREATE TABLE User_Locations (
                                user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
                                location_id INTEGER NOT NULL REFERENCES Locations(id) ON DELETE CASCADE,
                                PRIMARY KEY (user_id, location_id)
);