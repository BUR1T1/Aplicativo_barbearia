using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app;

var builder = WebApplication.CreateBuilder(args);

// Configura o DbContext com SQL Server remoto
builder.Services.AddDbContext<BarberTechContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("RemoteConnection")));

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();

app.UseAuthorization();
app.MapControllers();

app.Run();
