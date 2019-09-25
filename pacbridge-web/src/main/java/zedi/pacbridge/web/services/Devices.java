package zedi.pacbridge.web.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.json.JSONObject;

import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.devices.DeviceObjectCreator;
import zedi.pacbridge.app.devices.KeyDecoder;
import zedi.pacbridge.app.devices.SecretKeyDecoderException;
import zedi.pacbridge.web.dtos.DeviceDTO;

@Path("/devices")
public class Devices {
    @Inject
    private DeviceCache cache;

    @GET
    @Path("/")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public Collection<DeviceDTO> allDevices() {
        Collection<Device> ors = cache.allCachedDevices();
        List<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
        for (Iterator<Device> iter = ors.iterator(); iter.hasNext(); ) 
            dtos.add(new DeviceDTO(iter.next().getNuid(), iter.next().getFirmwareVersion()));
        return dtos;
    }
    
    @GET
    @Path("/{id}")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public DeviceDTO getDeviceInfo(@PathParam("id") String nuid) {
        Device device = cache.deviceForNetworkUnitId(nuid);
        return (device == null) ? null : new DeviceDTO(nuid, device.getFirmwareVersion()); 
    }
    
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON) 
    public String deleteDevice(@PathParam("id") String nuid) {
        StringBuilder stringBuilder = new StringBuilder();
        if (cache.deleteDeviceWithNuid(nuid))
            stringBuilder.append("{status: 'Success'}");
        else
            stringBuilder.append("Failure'}");
        return stringBuilder.toString();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public String addDevice(DeviceDTO dto) {
        JSONObject json = new JSONObject();
        KeyDecoder keyDecoder = new KeyDecoder();
        try {
            keyDecoder.decodedBytesForBase64EncodedBytes(dto.getSecretKey().getBytes());
            DeviceObjectCreator deviceCreator = new DeviceObjectCreator(keyDecoder);
            Device device = deviceCreator.objectForStuff(dto.getId(), dto.getSecretKey().getBytes(), dto.getNetworkNumber(), new Timestamp(System.currentTimeMillis()));
            if (device != null) { 
                cache.addDevice(device);
                json.put("status", "Success");
            } else { 
                json.put("status", "Failure");
                json.put("message", "Unable to create device");
            }
        } catch (SecretKeyDecoderException e) {
            json.put("status", "Failure");
            json.put("message", e.toString());
        }
        return json.toString();
    }
}
