const {
    Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
    HeadingLevel, AlignmentType, BorderStyle, WidthType, ShadingType,
    LevelFormat, PageBreak
} = require('docx');
const fs = require('fs');

// ── colours ──────────────────────────────────────────────────────────────────
const NAVY   = "1B2A4A";
const BLUE   = "2E5FA3";
const LIGHT  = "D6E4F7";
const MINT   = "E8F5E9";
const YELLOW = "FFF9C4";
const WHITE  = "FFFFFF";
const GREY   = "F5F5F5";
const GREEN  = "2E7D32";
const ORANGE = "E65100";
const RED    = "C62828";

const border = { style: BorderStyle.SINGLE, size: 1, color: "BBBBBB" };
const borders = { top: border, bottom: border, left: border, right: border };

function h(text, level, color = NAVY) {
    return new Paragraph({
        heading: level,
        spacing: { before: 300, after: 120 },
        children: [new TextRun({ text, bold: true, color, font: "Arial",
            size: level === HeadingLevel.HEADING_1 ? 36 : level === HeadingLevel.HEADING_2 ? 28 : 24 })]
    });
}

function p(text, options = {}) {
    return new Paragraph({
        spacing: { after: 120 },
        children: [new TextRun({ text, font: "Arial", size: 22, ...options })]
    });
}

function code(text) {
    return new Paragraph({
        spacing: { after: 80, before: 80 },
        shading: { fill: "1E1E1E", type: ShadingType.CLEAR },
        indent: { left: 400, right: 400 },
        children: [new TextRun({ text, font: "Courier New", size: 18, color: "98C379" })]
    });
}

function badge(label, color) {
    return new TableCell({
        borders,
        shading: { fill: color, type: ShadingType.CLEAR },
        margins: { top: 80, bottom: 80, left: 200, right: 200 },
        width: { size: 1800, type: WidthType.DXA },
        children: [new Paragraph({
            alignment: AlignmentType.CENTER,
            children: [new TextRun({ text: label, bold: true, font: "Arial", size: 18, color: WHITE })]
        })]
    });
}

function cell(text, fill = WHITE, bold = false, w = 4680) {
    return new TableCell({
        borders,
        shading: { fill, type: ShadingType.CLEAR },
        margins: { top: 80, bottom: 80, left: 160, right: 160 },
        width: { size: w, type: WidthType.DXA },
        children: [new Paragraph({ children: [new TextRun({ text, bold, font: "Arial", size: 20 })] })]
    });
}

function headerCell(text, w = 4680) {
    return cell(text, NAVY, true, w);
}

// ─────────────────────────────────────────────────────────────────────────────
// MIND MAP  (hand-crafted SVG rendered as description paragraphs with box table)
// We'll create a visual ASCII-style mind map using a 3-column table layout
// ─────────────────────────────────────────────────────────────────────────────

function mindMapBox(text, fill, textColor = WHITE, w = 2800) {
    return new TableCell({
        borders,
        shading: { fill, type: ShadingType.CLEAR },
        margins: { top: 140, bottom: 140, left: 200, right: 200 },
        width: { size: w, type: WidthType.DXA },
        verticalAlign: "center",
        children: [new Paragraph({
            alignment: AlignmentType.CENTER,
            children: [new TextRun({ text, bold: true, font: "Arial", size: 20, color: textColor })]
        })]
    });
}

function blankCell(w = 400) {
    return new TableCell({
        borders: { top: { style: BorderStyle.NONE }, bottom: { style: BorderStyle.NONE },
            left: { style: BorderStyle.NONE }, right: { style: BorderStyle.NONE } },
        width: { size: w, type: WidthType.DXA },
        children: [new Paragraph({ children: [new TextRun("")] })]
    });
}

function arrowCell(w = 600) {
    return new TableCell({
        borders: { top: { style: BorderStyle.NONE }, bottom: { style: BorderStyle.NONE },
            left: { style: BorderStyle.NONE }, right: { style: BorderStyle.NONE } },
        width: { size: w, type: WidthType.DXA },
        children: [new Paragraph({
            alignment: AlignmentType.CENTER,
            children: [new TextRun({ text: "-->", bold: true, font: "Arial", size: 22, color: BLUE })]
        })]
    });
}

// ─────────────────────────────────────────────────────────────────────────────
// BUILD MIND MAP TABLE
// Layout:
//  [AUTH SERVICE] --> [POST /auth/register] --> [Returns UUID] --> [CLIENT holds UUID]
//                                                                        |
//                                                              [POST /api/users (User-SVC)]
//                                                                        |
//                                                            [UserServiceImpl.createUser()]
//                                                            [Saves to bug_ai_user schema]
// ─────────────────────────────────────────────────────────────────────────────
function buildMindMap() {
    const rows = [
        // Title row
        new TableRow({
            children: [
                new TableCell({
                    columnSpan: 9,
                    borders,
                    shading: { fill: NAVY, type: ShadingType.CLEAR },
                    margins: { top: 200, bottom: 200, left: 300, right: 300 },
                    width: { size: 9360, type: WidthType.DXA },
                    children: [new Paragraph({
                        alignment: AlignmentType.CENTER,
                        children: [new TextRun({ text: "AUTH-SERVICE  ←→  USER-SERVICE  CONNECTION FLOW", bold: true, font: "Arial", size: 26, color: WHITE })]
                    })]
                })
            ]
        }),
        // Row 1: Client --> Auth --> UUID returned
        new TableRow({
            height: { value: 700, rule: "exact" },
            children: [
                mindMapBox("CLIENT\n(Postman / Frontend)", "4A4A8A", WHITE, 1600),
                arrowCell(),
                mindMapBox("POST /auth/register\nAuth Service :8081", BLUE, WHITE, 2200),
                arrowCell(),
                mindMapBox("Validates credentials\nHashes password (BCrypt)\nGenerates UUID", NAVY, WHITE, 2400),
                arrowCell(),
                mindMapBox("Returns UUID\n+ success response", GREEN, WHITE, 1800),
            ]
        }),
        // Spacer
        new TableRow({
            height: { value: 200, rule: "exact" },
            children: [new TableCell({
                columnSpan: 9,
                borders: { top: { style: BorderStyle.NONE }, bottom: { style: BorderStyle.NONE },
                    left: { style: BorderStyle.NONE }, right: { style: BorderStyle.NONE } },
                width: { size: 9360, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("    ↓  Client stores UUID")] })]
            })]
        }),
        // Row 2: Client sends UUID to User Service
        new TableRow({
            height: { value: 700, rule: "exact" },
            children: [
                mindMapBox("CLIENT sends UUID\n+ profile data", "4A4A8A", WHITE, 1600),
                arrowCell(),
                mindMapBox("POST /api/users\nUser Service :8082", "E65100", WHITE, 2200),
                arrowCell(),
                mindMapBox("createUser()\nChecks duplicate email\nSets UUID as PK", "1B6B3A", WHITE, 2400),
                arrowCell(),
                mindMapBox("Saved to\nbug_ai_user schema", "5D4037", WHITE, 1800),
            ]
        }),
        // Spacer
        new TableRow({
            height: { value: 200, rule: "exact" },
            children: [new TableCell({
                columnSpan: 9,
                borders: { top: { style: BorderStyle.NONE }, bottom: { style: BorderStyle.NONE },
                    left: { style: BorderStyle.NONE }, right: { style: BorderStyle.NONE } },
                width: { size: 9360, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("")] })]
            })]
        }),
        // Legend row
        new TableRow({
            children: [
                new TableCell({
                    columnSpan: 9,
                    borders,
                    shading: { fill: GREY, type: ShadingType.CLEAR },
                    margins: { top: 120, bottom: 120, left: 300, right: 300 },
                    width: { size: 9360, type: WidthType.DXA },
                    children: [
                        new Paragraph({
                            children: [new TextRun({ text: "KEY DESIGN PRINCIPLE: ", bold: true, font: "Arial", size: 20, color: NAVY }),
                                new TextRun({ text: "Auth-Service OWNS UUID generation. User-Service RECEIVES UUID as primary key. No shared DB. No direct service call. The client acts as the bridge (Phase 1).", font: "Arial", size: 20 })]
                        })
                    ]
                })
            ]
        }),
    ];

    return new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: [1600, 600, 2200, 600, 2400, 600, 1800, 0, 0],
        rows,
    });
}

// API Flow Table
function buildApiTable() {
    return new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: [1800, 3200, 1280, 3080],
        rows: [
            new TableRow({ children: [
                    headerCell("Method", 1800), headerCell("Endpoint", 3200),
                    headerCell("Status", 1280), headerCell("Description", 3080)
                ]}),
            new TableRow({ children: [
                    cell("POST", MINT, false, 1800), cell("/api/users", WHITE, false, 3200),
                    cell("201", WHITE, false, 1280), cell("Create user (called after register)", WHITE, false, 3080)
                ]}),
            new TableRow({ children: [
                    cell("GET", LIGHT, false, 1800), cell("/api/users/{id}", WHITE, false, 3200),
                    cell("200 / 404", WHITE, false, 1280), cell("Get user by UUID", WHITE, false, 3080)
                ]}),
            new TableRow({ children: [
                    cell("GET", LIGHT, false, 1800), cell("/api/users/email/{email}", WHITE, false, 3200),
                    cell("200 / 404", WHITE, false, 1280), cell("Lookup by email", WHITE, false, 3080)
                ]}),
            new TableRow({ children: [
                    cell("GET", LIGHT, false, 1800), cell("/api/users", WHITE, false, 3200),
                    cell("200", WHITE, false, 1280), cell("List all users", WHITE, false, 3080)
                ]}),
            new TableRow({ children: [
                    cell("PUT", YELLOW, false, 1800), cell("/api/users/{id}", WHITE, false, 3200),
                    cell("200 / 404", WHITE, false, 1280), cell("Update name + role", WHITE, false, 3080)
                ]}),
            new TableRow({ children: [
                    cell("PATCH", YELLOW, false, 1800), cell("/api/users/{id}/deactivate", WHITE, false, 3200),
                    cell("204 / 404", WHITE, false, 1280), cell("Soft-delete (active=false)", WHITE, false, 3080)
                ]}),
            new TableRow({ children: [
                    cell("DELETE", "FFEBEE", false, 1800), cell("/api/users/{id}", WHITE, false, 3200),
                    cell("204 / 404", WHITE, false, 1280), cell("Hard delete user record", WHITE, false, 3080)
                ]}),
        ]
    });
}

// Layer breakdown table
function buildLayerTable() {
    return new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: [2200, 3000, 4160],
        rows: [
            new TableRow({ children: [
                    headerCell("Layer", 2200), headerCell("Class / File", 3000), headerCell("Responsibility", 4160)
                ]}),
            new TableRow({ children: [
                    cell("Entity", GREY, false, 2200), cell("User.java", WHITE, false, 3000), cell("JPA entity mapped to bug_ai_user.users", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("Enum", GREY, false, 2200), cell("Role.java", WHITE, false, 3000), cell("ADMIN, DEVELOPER, QA, PROJECT_MANAGER", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("Repository", LIGHT, false, 2200), cell("UserRepository", WHITE, false, 3000), cell("findByEmail, existsByEmail, CRUD", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("DTO Request", MINT, false, 2200), cell("CreateUserRequest", WHITE, false, 3000), cell("Accepts UUID from client + profile fields", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("DTO Request", MINT, false, 2200), cell("UpdateUserRequest", WHITE, false, 3000), cell("fullName + role updates only", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("DTO Response", MINT, false, 2200), cell("UserResponse", WHITE, false, 3000), cell("Safe public view — no password fields", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("Service", YELLOW, false, 2200), cell("UserService (interface)", WHITE, false, 3000), cell("Contract definition", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("Service Impl", YELLOW, false, 2200), cell("UserServiceImpl", WHITE, false, 3000), cell("Business logic, @Transactional writes", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("Controller", "FFE0B2", false, 2200), cell("UserController", WHITE, false, 3000), cell("REST endpoints, validation, HTTP codes", WHITE, false, 4160)
                ]}),
            new TableRow({ children: [
                    cell("Exception", "FFEBEE", false, 2200), cell("GlobalExceptionHandler", WHITE, false, 3000), cell("UserNotFoundException, DuplicateEmail → structured JSON", WHITE, false, 4160)
                ]}),
        ]
    });
}

// ─── DOCUMENT ────────────────────────────────────────────────────────────────
const doc = new Document({
    styles: {
        default: { document: { run: { font: "Arial", size: 22 } } },
        paragraphStyles: [
            { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
                run: { size: 36, bold: true, color: NAVY, font: "Arial" },
                paragraph: { spacing: { before: 360, after: 200 }, outlineLevel: 0 } },
            { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
                run: { size: 28, bold: true, color: BLUE, font: "Arial" },
                paragraph: { spacing: { before: 280, after: 160 }, outlineLevel: 1 } },
            { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "Normal", quickFormat: true,
                run: { size: 24, bold: true, color: "444444", font: "Arial" },
                paragraph: { spacing: { before: 200, after: 120 }, outlineLevel: 2 } },
        ]
    },
    sections: [{
        properties: {
            page: { size: { width: 12240, height: 15840 }, margin: { top: 1080, right: 1080, bottom: 1080, left: 1080 } }
        },
        children: [
            // Cover
            new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 800, after: 200 },
                children: [new TextRun({ text: "BugAI Tracker — M.Tech Project", bold: true, font: "Arial", size: 20, color: "888888" })] }),
            new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 0, after: 120 },
                children: [new TextRun({ text: "USER SERVICE", bold: true, font: "Arial", size: 52, color: NAVY })] }),
            new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 0, after: 60 },
                children: [new TextRun({ text: "Complete Implementation + Auth-Service Connection", font: "Arial", size: 28, color: BLUE })] }),
            new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 0, after: 600 },
                children: [new TextRun({ text: "Java 21 · Spring Boot · Spring Data JPA · MySQL", font: "Arial", size: 22, color: "666666" })] }),

            // ── Section 1: Connection Mind Map
            h("1. Auth-Service → User-Service Connection Mind Map", HeadingLevel.HEADING_1),
            p("The diagram below shows the exact flow of data between the Auth and User services in Phase 1. The client acts as the bridge — there is NO direct service-to-service HTTP call."),
            new Paragraph({ spacing: { after: 200 } }),
            buildMindMap(),
            new Paragraph({ spacing: { after: 300 } }),

            // Step by step
            h("2. Step-by-Step Registration Flow", HeadingLevel.HEADING_2),
            p("Step 1 — Client calls Auth Service:"),
            code('POST http://localhost:8081/api/auth/register'),
            code('Body: { "email": "uzma@bugai.com", "password": "Secret@123" }'),
            new Paragraph({ spacing: { after: 100 } }),
            p("Step 2 — Auth Service responds with UUID:"),
            code('{ "userId": "550e8400-e29b-41d4-a716-446655440000", "message": "Registration successful" }'),
            new Paragraph({ spacing: { after: 100 } }),
            p("Step 3 — Client calls User Service with that UUID:"),
            code('POST http://localhost:8082/api/users'),
            code('Body: { "id": "550e8400-...", "fullName": "Uzma", "email": "uzma@bugai.com", "role": "DEVELOPER" }'),
            new Paragraph({ spacing: { after: 100 } }),
            p("Step 4 — User Service saves user with that UUID as primary key. No UUID is generated in User Service."),
            new Paragraph({ spacing: { after: 300 } }),

            // ── Section 3: Layer Table
            h("3. Service Layer Breakdown", HeadingLevel.HEADING_1),
            buildLayerTable(),
            new Paragraph({ spacing: { after: 300 } }),

            // ── Section 4: API Reference
            h("4. REST API Reference", HeadingLevel.HEADING_1),
            buildApiTable(),
            new Paragraph({ spacing: { after: 300 } }),

            // ── Section 5: Key Decisions
            h("5. Key Design Decisions", HeadingLevel.HEADING_1),
            new Table({
                width: { size: 9360, type: WidthType.DXA },
                columnWidths: [3600, 5760],
                rows: [
                    new TableRow({ children: [headerCell("Decision", 3600), headerCell("Reason", 5760)] }),
                    new TableRow({ children: [cell("UUID as PK, not auto-increment", GREY, false, 3600), cell("UUID comes from auth-service — enforces shared identity across services without a shared DB", WHITE, false, 5760)] }),
                    new TableRow({ children: [cell("No @Autowired — use @RequiredArgsConstructor", GREY, false, 3600), cell("Constructor injection is safer, more testable, and IntelliJ-friendly with Lombok", WHITE, false, 5760)] }),
                    new TableRow({ children: [cell("@Transactional on all writes", GREY, false, 3600), cell("Ensures atomicity — partial saves are rolled back automatically", WHITE, false, 5760)] }),
                    new TableRow({ children: [cell("@Builder.Default for active=true", GREY, false, 3600), cell("Without @Builder.Default, Lombok builder ignores field initializers", WHITE, false, 5760)] }),
                    new TableRow({ children: [cell("Validation on DTO, not Entity", GREY, false, 3600), cell("JPA entities are persistence objects — mixing concerns leads to brittle code", WHITE, false, 5760)] }),
                    new TableRow({ children: [cell("Schema: bug_ai_user (separate)", GREY, false, 3600), cell("Each microservice owns its schema — no cross-schema JOINs allowed", WHITE, false, 5760)] }),
                ]
            }),
            new Paragraph({ spacing: { after: 400 } }),

            // Footer note
            new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { before: 400 },
                children: [new TextRun({ text: "BugAI Tracker · Phase 1 · User Service Documentation", font: "Arial", size: 18, color: "AAAAAA" })]
            }),
        ]
    }]
});

Packer.toBuffer(doc).then(buf => {
    fs.writeFileSync('user-service-docs.docx', buf);
    console.log('user-service-docs.docx created');
});