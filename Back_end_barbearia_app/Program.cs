using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app;

var builder = WebApplication.CreateBuilder(args);


builder.Services.AddDbContext<BarberTechContext>(options =>
    options.UseSqlite(builder.Configuration.GetConnectionString("RemoteConnection"))
);

builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(5260); 
});

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAndroidApp", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// ❌ Não forçar HTTPS durante o desenvolvimento Android
// app.UseHttpsRedirection();

// ✅ Aplicar CORS antes dos controllers
app.UseCors("AllowAndroidApp");

app.UseAuthorization();
app.MapControllers();

app.Run();
