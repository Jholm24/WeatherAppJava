CREATE TABLE IF NOT EXISTS WeatherSources (
    id      serial PRIMARY KEY,
    name    varchar(50) not null unique
);

INSERT INTO WeatherSources (name) VALUES ('openweather'), ('googleweather')
ON CONFLICT (name) DO NOTHING;

CREATE TABLE IF NOT EXISTS GeoAddresses (
    id          serial PRIMARY KEY,
    address     varchar(255)    not null unique,
    latitude    decimal(9,6)    not null,
    longitude   decimal(9,6)    not null,
    created_at  timestamp       default now()
);

CREATE TABLE IF NOT EXISTS WeatherReadings (
    id              serial PRIMARY KEY,
    address_id      int             not null REFERENCES GeoAddresses(id),
    source_id       int             not null REFERENCES WeatherSources(id),
    temp            decimal(5,2)    not null,
    humidity        int             not null,
    windDirection   varchar(3)      not null,
    windSpeed       decimal(5,2)    not null,
    cloudCover      varchar(255),
    created_at      timestamp       default now()
);
