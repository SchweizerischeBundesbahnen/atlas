-- Line
CREATE SEQUENCE line_version_seq START WITH 1000 INCREMENT BY 1;

create table line_version
(
    id                    bigint           not null
        primary key,
    swiss_line_number     varchar(50),
    slnid                 varchar(500)     not null,
    status                varchar(50)      not null,
    line_type             varchar(50)      not null,
    payment_type          varchar(50),
    number                varchar(50)      not null,
    alternative_name      varchar(50),
    combination_name      varchar(50),
    long_name             varchar(255),
    color_font_rgb        varchar(50),
    color_back_rgb        varchar(50),
    color_font_cmyk       varchar(50),
    color_back_cmyk       varchar(50),
    icon                  varchar(255),
    description           varchar(255)     not null,
    valid_from            date             not null,
    valid_to              date             not null,
    business_organisation varchar(50)      not null,
    comment               varchar(1500),
    creation_date         timestamp        not null,
    creator               varchar(50),
    edition_date          timestamp        not null,
    editor                varchar(50),
    version               bigint default 0 not null,
    concession_type       varchar(50),
    offer_category        varchar(50),
    short_number          varchar(10),
    constraint line_slnid_unique
        unique (slnid, valid_from)
);

-- Subline
CREATE SEQUENCE subline_version_seq START WITH 1000 INCREMENT BY 1;

create table subline_version
(
    id                    bigint           not null
        primary key,
    swiss_subline_number  varchar(50),
    mainline_slnid        varchar(500),
    slnid                 varchar(500)     not null,
    status                varchar(50)      not null,
    subline_type          varchar(50)      not null,
    description           varchar(255)     not null,
    number                varchar(50),
    long_name             varchar(255),
    payment_type          varchar(50),
    valid_from            date             not null,
    valid_to              date             not null,
    business_organisation varchar(50)      not null,
    creation_date         timestamp        not null,
    creator               varchar(50),
    edition_date          timestamp        not null,
    editor                varchar(50),
    version               bigint default 0 not null,
    concession_type       varchar(50),
    constraint subline_slnid_unique
        unique (slnid, valid_from)
);