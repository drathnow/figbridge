package zedi.pacbridge.utl;

public interface SiteAddressSerializer<T> {
    public SiteAddress siteAddressFor(T addressObject);
    public T objectForSiteAddress(SiteAddress siteAddress);
}
